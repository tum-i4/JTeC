import argparse
import os
import re
import sys
import threading
import time
from pathlib import Path
from shlex import split

import frida
from frida_tools.application import Reactor

class FridaApplication(object):
    """
    Taken from: https://github.com/frida/frida-python/blob/main/examples/child_gating.py
    """

    def __init__(self, tracing_log: Path, includes_regex: str):
        self._stop_requested = threading.Event()
        self._reactor = Reactor(
            run_until_return=lambda reactor: self._stop_requested.wait()
        )

        self._device = frida.get_local_device()
        self._sessions = set()

        # self._device.on(
        #     "child-added",
        #     lambda child: self._reactor.schedule(lambda: self._on_child_added(child)),
        # )
        # self._device.on(
        #     "child-removed",
        #     lambda child: self._reactor.schedule(lambda: self._on_child_removed(child)),
        # )

        self._tracing_log = tracing_log
        self._includes_regex = re.compile(includes_regex)

    def run_command(self, command: str):
        self._reactor.schedule(lambda: self._start(command))
        self._reactor.run()

    def run_process(self, process_name: str):
        pid: int = -1
        for proc in self._device.enumerate_processes():
            if process_name in [str(proc.pid), proc.name]:
                # we're simply using the first match
                pid = proc.pid
                print(f"Found process {proc.name} ({proc.pid}) for {process_name}.")
                break
        if pid == -1:
            print(f"Could not find process identifier {process_name}.", file=sys.stderr)
            self._stop_requested.set()
        else:
            self._reactor.schedule(lambda: self._instrument(pid))
            self._reactor.run()

    def _start(self, command: str):
        try:
            pid = self._device.spawn(split(command))
            self._instrument(pid, True)
        except Exception as e:
            print(f"Failed to spawn process for ({command}): {e}", file=sys.stderr)
            self._stop_requested.set()

    def _stop_if_idle(self):
        if len(self._sessions) == 0:
            self._stop_requested.set()

    def _instrument(self, pid, resume=False):
        print("attach(pid={})".format(pid))
        session = self._device.attach(pid)
        session.on(
            "detached",
            lambda reason: self._reactor.schedule(
                lambda: self._on_detached(pid, session, reason)
            ),
        )
        # TODO: Once child gating is more stable, we can follow child processes here as well...
        # print("enable_child_gating()")
        # session.enable_child_gating()
        print("create_script()")
        script = session.create_script(r"""
"use strict";

/**
 * Utility to only include file paths that are actual files (no directories) and exclude log files.
 * @param filepath
 */
const sanitizeFilePath = (filepath) => {
    // We filter out irrelevant log files and only keep actual files, no directories.
    if (!filepath.endsWith('.log') && filepath !== "." && filepath.includes('.')) {
        // Note: This is only the best guess, simply taking the current working directory and appending the filename.
        if (Process.platform === "windows" && !filepath.includes("\\")) {
            return `${Process.getCurrentDir()}\\${filepath}`;
        } else if (Process.platform !== "windows" && !filepath.includes("/")) {
            return `${Process.getCurrentDir()}/${filepath}`;
        }
        return filepath;
    }
    return null;
};
/**
 * Utility to send a file path to host if it is valid.
 * @param filepath
 */
const sendFilePath = (filepath) => {
    if (filepath !== null) {
        const sanitizedFilePath = sanitizeFilePath(filepath);
        if (sanitizedFilePath !== null) {
            send({ syscall: sanitizedFilePath });
        }
    }
};
/**
 * Instruments `open` system calls for Linux, macOS, and Windows.
 */
const instrumentSyscalls = () => {
    const openFilePtrs = [];
    const callbacks = [];
    switch (Process.platform) {
        case "linux": {
            const libcLikeModules = new ModuleMap((m) => {
                const matches = m.name.match("libc(|-\\d+(\\.\\d+)*).so");
                return matches !== null && matches.length > 0;
            }).values();
            if (libcLikeModules.length > 0) {
                const libcModule = libcLikeModules[0];
                const callback = (argIdx) => (args) => {
                    const filepath = args[argIdx].readUtf8String();
                    sendFilePath(filepath);
                };
                const pOpenFile = Module.findExportByName(libcModule.name, 'open');
                if (pOpenFile !== null) {
                    openFilePtrs.push(pOpenFile);
                    callbacks.push(callback(0));
                }
                const pOpenAtFile = Module.findExportByName(libcModule.name, 'openat');
                if (pOpenAtFile !== null) {
                    openFilePtrs.push(pOpenAtFile);
                    callbacks.push(callback(1));
                }
            }
            break;
        }
        case "windows": {
            const callback = (args) => {
                // See: https://docs.microsoft.com/en-us/windows/win32/api/ntdef/ns-ntdef-_object_attributes
                const pObjectAttributes = args[2];
                const pObjectName = pObjectAttributes
                    .add(4) // ULONG Length
                    .add(4) // 4 bytes padding for pointer to be 8-byte aligned (64-bit)
                    .add(Process.pointerSize) // HANDLE RootDirectory
                    .readPointer();
                const filepath = pObjectName
                    .add(2) // USHORT Length
                    .add(2) // USHORT MaximumLength
                    .add(4) // 4 bytes padding for pointer to be 8-byte aligned (64-bit)
                    .readPointer()
                    .readUtf16String(); // Win32 has wide-strings
                sendFilePath(filepath);
            };
            const pCreateFile = Module.findExportByName('ntdll.dll', 'NtCreateFile');
            if (pCreateFile !== null) {
                openFilePtrs.push(pCreateFile);
                callbacks.push(callback);
            }
            const pOpenFile = Module.findExportByName('ntdll.dll', 'NtOpenFile');
            if (pOpenFile !== null) {
                openFilePtrs.push(pOpenFile);
                callbacks.push(callback);
            }
            break;
        }
        case "darwin": {
            const pOpenFile = Module.findExportByName('libsystem_kernel.dylib', 'open');
            const callback = (argIdx) => (args) => {
                const filepath = ptr(args[argIdx]).readUtf8String();
                sendFilePath(filepath);
            };
            if (pOpenFile !== null) {
                openFilePtrs.push(pOpenFile);
                callbacks.push(callback(0));
            }
            const pOpenNoCancelFile = Module.findExportByName('libsystem_kernel.dylib', '__open_nocancel');
            if (pOpenNoCancelFile !== null) {
                openFilePtrs.push(pOpenNoCancelFile);
                callbacks.push(callback(0));
            }
            const pOpenAtFile = Module.findExportByName('libsystem_kernel.dylib', 'openat');
            if (pOpenAtFile !== null) {
                openFilePtrs.push(pOpenAtFile);
                callbacks.push(callback(1));
            }
            break;
        }
        default:
            return;
    }
    for (const i in openFilePtrs) {
        Interceptor.attach(openFilePtrs[i], {
            onEnter: callbacks[i]
        });
    }
};

instrumentSyscalls();
""")
        script.on(
            "message",
            lambda message, data: self._reactor.schedule(
                lambda: self._on_message(pid, message)
            ),
        )
        print("load()")
        script.load()
        if resume:
            print("resume(pid={})".format(pid))
            self._device.resume(pid)
        self._sessions.add(session)

    def _on_child_added(self, child):
        print("child_added: {}".format(child.pid))
        self._instrument(child.pid)
        self._reactor.schedule(
            lambda: self._write_tracing_log(
                '{"timestamp": %d, "pid": "%s_%d", "action": "SPAWN", "target": "PROCESS", "value": "%s_%d"}'
                % (time.time_ns(), "java", child.parent_pid, "", child.pid)
            )
        )

    def _on_child_removed(self, child):
        print("child_removed: {}".format(child.pid))

    def _on_detached(self, pid, session, reason):
        print("detached: pid={}, reason='{}'".format(pid, reason))
        self._sessions.remove(session)
        if reason == "process-replaced":
            self._instrument(pid)
        self._reactor.schedule(self._stop_if_idle, delay=0.5)

    def _write_tracing_log(self, message):
        with self._tracing_log.open(mode="a+") as f:
            f.write(f"{message}\n")

    def _on_message(self, pid, message):
        if "payload" in message and "syscall" in message["payload"]:
            filepath: str = message["payload"]["syscall"]
            if self._includes_regex.match(filepath):
                self._reactor.schedule(
                    lambda: self._write_tracing_log(
                        '{"timestamp": %d, "pid": "%s_%d", "action": "OPEN", "target": "FILE", "value": "%s"}'
                        % (time.time_ns(), "java", pid, filepath)
                    )
                )


def parse_arguments():
    """
    Define and parse program arguments.
    :return: arguments captured in object.
    """
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--target",
        "-t",
        help="Target executable to spawn.",
    )
    parser.add_argument(
        "--process",
        "-p",
        help="Process identifier or name to be traced.",
    )
    parser.add_argument(
        "--includes",
        "-i",
        help="Regex for filtering matched files.",
        default=rf".*\{os.sep}.*\..*",
    )
    parser.add_argument(
        "--output", "-o", help="Output file", default=f"{os.getpid()}_syscalls.log"
    )
    return parser.parse_args()


def main():
    args = parse_arguments()

    if args.process is None and args.target is None:
        print("Missing process identifier or target executable.", file=sys.stderr)
        exit(1)

    output_file: Path = Path(args.output).resolve()
    if output_file.exists():
        output_file.unlink()
    output_file.touch()

    app = FridaApplication(tracing_log=output_file, includes_regex=args.includes)
    if args.target is not None:
        app.run_command(args.target)
    else:
        app.run_process(args.process)


if __name__ == "__main__":
    main()
