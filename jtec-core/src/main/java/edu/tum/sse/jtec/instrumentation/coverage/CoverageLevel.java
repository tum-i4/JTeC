package edu.tum.sse.jtec.instrumentation.coverage;

public enum CoverageLevel {
    CLASS("CLASS"),
    METHOD("METHOD");

    private final String coverageLevel;

    CoverageLevel(final String coverageLevel) {
        this.coverageLevel = coverageLevel;
    }

    @Override
    public String toString() {
        return coverageLevel;
    }
}
