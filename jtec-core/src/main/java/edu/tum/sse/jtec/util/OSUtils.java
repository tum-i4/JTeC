package edu.tum.sse.jtec.util;

public final class OSUtils {
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }
}
