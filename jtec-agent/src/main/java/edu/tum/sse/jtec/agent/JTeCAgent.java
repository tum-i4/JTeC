package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.util.ProcessUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.instrument.Instrumentation;
import java.util.Collections;

public class JTeCAgent {

    private static final String PID_KEY = "JTEC_PID";

    public static void premain(String arguments, Instrumentation instrumentation) {
        if (arguments == null) {
            throw new RuntimeException("Missing arguments with log file path for instrumentation.");
        }
        AgentOptions options = AgentOptions.fromString(arguments);
        System.err.println("Attaching JTeC agent with args: " + options.toAgentString());

        // Before we start tracing, we need to run the pre-test command, if specified.
        if (!options.getPreTestCommand().isEmpty()) {
            try {
                Process process = ProcessUtils.run(options.getPreTestCommand(),
                        Collections.singletonMap(PID_KEY, ProcessUtils.getCurrentPid()), false);
                // Since the subprocess is non-blocking, we collect the output in a separate thread.
                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.err.println(line);
                        }
                        process.waitFor();
                    } catch (Exception e) {
                        System.err.println("Failed to read process output: " + e.getMessage());
                    }
                }).start();
            } catch (Exception e) {
                System.err.println(
                        "Failed to run pre-test command " + options.getPreTestCommand() + " : " + e.getMessage());
            }
        }

        new Tracer(instrumentation, options);
    }
}
