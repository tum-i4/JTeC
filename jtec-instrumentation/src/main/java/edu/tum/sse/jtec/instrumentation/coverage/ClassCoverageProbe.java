package edu.tum.sse.jtec.instrumentation.coverage;

public class ClassCoverageProbe extends CoverageProbe {
    private final String className;

    public ClassCoverageProbe(String coverageRunId, String className) {
        super(coverageRunId);
        this.className = className;
    }

    @Override
    public String getProbeId() {
        return className;
    }
}
