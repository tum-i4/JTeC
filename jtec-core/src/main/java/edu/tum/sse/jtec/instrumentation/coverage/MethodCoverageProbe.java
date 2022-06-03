package edu.tum.sse.jtec.instrumentation.coverage;

public class MethodCoverageProbe extends CoverageProbe {
    private final String className;
    private final String methodSignature;
    private final String returnType;

    public MethodCoverageProbe(String coverageRunId, String className, String methodSignature, String returnType) {
        super(coverageRunId);
        this.className = className;
        this.methodSignature = methodSignature;
        this.returnType = returnType;
    }

    @Override
    String getProbeId() {
        return className + methodSignature + returnType;
    }
}
