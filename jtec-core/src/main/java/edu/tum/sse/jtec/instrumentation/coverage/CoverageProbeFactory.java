package edu.tum.sse.jtec.instrumentation.coverage;

/**
 * Factory to construct class- or method-level coverage probes.
 */
public interface CoverageProbeFactory {
    public ClassCoverageProbe createClassProbe(String className);
    public MethodCoverageProbe createMethodProbe(String className, String methodSignature, String returnType);
}
