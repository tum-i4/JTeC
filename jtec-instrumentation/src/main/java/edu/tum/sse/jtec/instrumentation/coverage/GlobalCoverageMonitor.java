package edu.tum.sse.jtec.instrumentation.coverage;

public class GlobalCoverageMonitor {

    private static CoverageMonitor coverageMonitor = null;

    public static void set(CoverageMonitor coverageMonitor) {
        GlobalCoverageMonitor.coverageMonitor = coverageMonitor;
    }

    public static boolean isMonitoringCoverage() {
        return coverageMonitor != null;
    }

    public static CoverageMonitor get() {
        if (coverageMonitor == null) {
            throw new RuntimeException(
                    "GlobalCoverageMonitor.set must be called before getting an instance.");
        }
        return coverageMonitor;
    }
}
