package edu.tum.sse.jtec.instrumentation.coverage;

public abstract class CoverageProbe {
    /**
     * An identifier for the coverage run (e.g., a process identifier).
     */
    private final String coverageRunId;
    private final long timestamp = System.currentTimeMillis();

    public CoverageProbe(String coverageRunId) {
        this.coverageRunId = coverageRunId;
    }

    abstract String getProbeId();

    public String getCoverageRunId() {
        return coverageRunId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
