package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.util.OSUtils;
import edu.tum.sse.jtec.util.ProcessUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.instrument.Instrumentation;
import java.util.Collections;


public class JTeCAgent {

    private static final String PID_KEY = "JTEC_PID";
    private static int agentCount = 0;

    public static void premain(String arguments, Instrumentation instrumentation) {
        if (arguments == null) {
            throw new RuntimeException("Missing arguments for JTeC agent.");
        }
        if (agentCount > 0) {
            return;
        }
        AgentOptions options = AgentOptions.fromString(arguments);
        String pid = ProcessUtils.getCurrentPid();
        System.err.println("Attaching JTeC agent (" + agentCount++ + ") to PID=" + pid + " with args: " + options.toAgentString());

        // Before we start tracing, we need to run the pre-test command, if specified.
        if (!options.getPreTestCommand().isEmpty()) {
            // Fix for `Jenkinsfile`s where batch scripts are defined as strings and escaping environment
            // variables with %% sometimes will not work.
            String preTestCommand = OSUtils.wrapEnvironmentVariable(options.getPreTestCommand(), PID_KEY);
            System.err.println("Executing preTestCommand: " + preTestCommand);
            try {
                Process process = ProcessUtils.run(preTestCommand, Collections.singletonMap(PID_KEY, pid), false);
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
                System.err.println("Failed to run pre-test command " + options.getPreTestCommand() + " : " + e.getMessage());
            }
        }

        new Tracer(instrumentation, options);
    }
}
