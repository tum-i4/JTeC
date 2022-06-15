package edu.tum.sse.jtec.instrumentation.util;

public class ProcessUtils {
    public static String getCurrentPid() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }
}
