package edu.tum.sse.jtec.util;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ProcessUtils {
    private static String currentPid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    public static Process run(String command, boolean blocking) throws IOException, InterruptedException {
        return run(command, Collections.emptyMap(), blocking);
    }

    public static Process run(String command, Map<String, String> env, boolean blocking) throws IOException, InterruptedException {
        List<String> commandParts;
        if (OSUtils.isWindows()) {
            commandParts = Arrays.asList("cmd", "/c", command);
        } else {
            commandParts = Arrays.asList("bash", "-c", command);
        }
        ProcessBuilder processBuilder = new ProcessBuilder(commandParts);
        Map<String, String> processEnv = processBuilder.environment();
        if (!env.isEmpty()) {
            processEnv.putAll(env);
        }
        // Merge stdout and stderr.
        processBuilder.redirectErrorStream(true);

        // Start process and either block until completion or directly return process instance.
        Process process = processBuilder.start();
        if (blocking) {
            process.waitFor();
        }
        return process;
    }

    public static String getCurrentPid() {
        return currentPid;
    }
}
