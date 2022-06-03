package edu.tum.sse.jtec.instrumentation.coverage;

import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.getCurrentPid;

/**
 * A coverage probe factory that collects coverage probes per PID of the currently running process.
 */
public class ProcessCoverageProbeFactory implements CoverageProbeFactory {

    @Override
    public ClassCoverageProbe createClassProbe(String className) {
        return new ClassCoverageProbe(getCurrentPid(), className);
    }

    @Override
    public MethodCoverageProbe createMethodProbe(String className, String methodSignature, String returnType) {
        return new MethodCoverageProbe(getCurrentPid(), className, methodSignature, returnType);
    }
}
