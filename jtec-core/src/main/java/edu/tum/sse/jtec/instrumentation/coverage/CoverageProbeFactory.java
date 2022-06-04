package edu.tum.sse.jtec.instrumentation.coverage;

/**
 * Factory to construct class- or method-level coverage probes.
 */
public interface CoverageProbeFactory {
    ClassCoverageProbe createClassProbe(String className);
    MethodCoverageProbe createMethodProbe(String className, String methodSignature, String returnType);
}
