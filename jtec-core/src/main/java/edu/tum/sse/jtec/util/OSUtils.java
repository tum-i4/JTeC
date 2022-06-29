package edu.tum.sse.jtec.util;


public final class OSUtils {
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static String wrapEnvironmentVariable(String command, String envVar) {
        if (isWindows()) {
            return command.replaceAll("([^%])(" + envVar + ")", "$1%$2%");
        } else {
            return command.replaceAll("([^${])(" + envVar + ")", "$1\\$\\{$2\\}");
        }
    }
}
