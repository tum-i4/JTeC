package edu.tum.sse.jtec.instrumentation;


/**
 * Utilities and constants for instrumentation classes.
 */
public class InstrumentationUtils {
    public static final String BYTEBUDDY_PACKAGE = "net.bytebuddy";
    public static final String JTEC_PACKAGE = "edu.tum.sse.jtec";

    public static String getCurrentPid() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }
}
