package edu.tum.sse.jtec.instrumentation.testevent.interceptors;

import edu.tum.sse.jtec.instrumentation.testevent.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;

public class JUnit4TestRunFinishedInterceptor {
    @Advice.OnMethodEnter
    public static void enter() {
        try {
            TestEventInterceptorUtility.testSuiteFinished();
        } catch (final Exception e) {
            System.err.println("Exception in test suite end is: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
