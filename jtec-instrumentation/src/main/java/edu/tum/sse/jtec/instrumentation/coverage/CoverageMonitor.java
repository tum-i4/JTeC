package edu.tum.sse.jtec.instrumentation.coverage;

public class CoverageMonitor {
    private final CoverageMap coverageMap = new CoverageMap();

    private CoverageMonitor() {
    }

    public static CoverageMonitor create() {
        return new CoverageMonitor();
    }

    public void registerClass(final String className) {
        registerCoverage(className);
    }

    public void registerMethodCall(final String className, final String methodSignature, final String returnType) {
        registerCoverage(className + "#" + methodSignature + returnType);
    }

    private void registerCoverage(String value) {
        coverageMap.put(value);
    }

    public void clearCoverage() {
        coverageMap.clear();
    }

    public CoverageMap getCoverageMap() {
        return coverageMap;
    }

    public void registerDump(final String dumpId) {
        coverageMap.dump(dumpId);
    }
}
