package edu.tum.sse.jtec.instrumentation.util;

public class ProcessUtils {
    public static String getInstrumentedPid() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }
}
