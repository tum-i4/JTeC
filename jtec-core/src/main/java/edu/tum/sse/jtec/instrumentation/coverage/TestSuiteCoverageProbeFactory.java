package edu.tum.sse.jtec.instrumentation.coverage;

import edu.tum.sse.jtec.instrumentation.testevent.TestEventInterceptorUtility;

import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.getCurrentPid;

/**
 * FIXME: A coverage probe factory that collects coverage probes per currently active test suite of the running process.
 */
public class TestSuiteCoverageProbeFactory implements CoverageProbeFactory {
    @Override
    public ClassCoverageProbe createClassProbe(String className) {
        // TODO: why is this not working?
        return new ClassCoverageProbe(getCurrentPid() + TestEventInterceptorUtility.currentTestSuite, className);
    }

    @Override
    public MethodCoverageProbe createMethodProbe(String className, String methodSignature, String returnType) {
        // TODO: why is this not working?
        return new MethodCoverageProbe(getCurrentPid() + TestEventInterceptorUtility.currentTestSuite, className, methodSignature, returnType);
    }
}
