package edu.tum.sse.jtec.instrumentation.coverage;

import edu.tum.sse.jtec.instrumentation.util.ProcessUtils;

/**
 * A coverage probe factory that collects coverage probes per PID of the currently running process.
 */
public class ProcessCoverageProbeFactory implements CoverageProbeFactory {

    @Override
    public ClassCoverageProbe createClassProbe(final String className) {
        return new ClassCoverageProbe(ProcessUtils.getInstrumentedPid(), className);
    }

    @Override
    public MethodCoverageProbe createMethodProbe(final String className, final String methodSignature, final String returnType) {
        return new MethodCoverageProbe(ProcessUtils.getInstrumentedPid(), className, methodSignature, returnType);
    }
}
