package edu.tum.sse.jtec.testlistener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AgentOptions {
    public static final String TRACE_TEST_EVENTS = "traceTestEvents";
    public static final String TEST_EVENT_OUT = "testEventOut";
    public static final String TRACE_SYS_EVENTS = "traceSysEvents";
    public static final String SYS_EVENT_OUT = "sysEventOut";
    public static final String DEFAULT_TEST_EVENT_OUT = "./testEvents.log";
    public static final String DEFAULT_SYS_EVENT_OUT = "./sysEvents.log";

    private boolean traceSystemEvents = false;

    private boolean traceTestEvents = false;
    private Path testEventOutputPath;

    private Path systemEventOutputPath;

    public static AgentOptions fromString(final String options) {
        final AgentOptions result = new AgentOptions();

        final String[] optionParts = options.split(",");
        final Map<String, String> optionsInput = new HashMap<>();
        for (final String part : optionParts) {
            final String[] keyValue = part.trim().split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException(part + " is not in the right format of key=value");
            }
            optionsInput.put(keyValue[0], keyValue[1]);
        }
        parseTestEventParams(result, optionsInput);
        parseSysEventParams(result, optionsInput);

        return result;
    }

    private static void parseTestEventParams(final AgentOptions result, final Map<String, String> optionsInput) {
        if (!optionsInput.containsKey(TRACE_TEST_EVENTS)) {
            return;
        }
        result.traceTestEvents = Boolean.parseBoolean(optionsInput.get(TRACE_TEST_EVENTS));
        result.testEventOutputPath = Paths.get(optionsInput.getOrDefault(TEST_EVENT_OUT, DEFAULT_TEST_EVENT_OUT)).toAbsolutePath();
        if (!result.testEventOutputPath.toFile().exists()) {
            try {
                Files.createFile(result.testEventOutputPath);
            } catch (final IOException exception) {
                System.err.println("Failed to open or create output file.");
                exception.printStackTrace();
            }
        }
    }

    private static void parseSysEventParams(final AgentOptions result, final Map<String, String> optionsInput) {
        if (!optionsInput.containsKey(TRACE_SYS_EVENTS)) {
            return;
        }
        result.traceSystemEvents = Boolean.parseBoolean(optionsInput.get(TRACE_SYS_EVENTS));
        result.systemEventOutputPath = Paths.get(optionsInput.getOrDefault(SYS_EVENT_OUT, DEFAULT_SYS_EVENT_OUT)).toAbsolutePath();
        if (!result.systemEventOutputPath.toFile().exists()) {
            try {
                Files.createFile(result.systemEventOutputPath);
            } catch (final IOException exception) {
                System.err.println("Failed to open or create output file.");
                exception.printStackTrace();
            }
        }
    }

    public Path getTestEventOutputPath() {
        return testEventOutputPath;
    }

    public Path getSystemEventOutputPath() {
        return systemEventOutputPath;
    }

    public boolean shouldTraceTestEvents() {
        return traceTestEvents;
    }

    public boolean shouldTraceSystemEvents() {
        return traceSystemEvents;
    }
}
