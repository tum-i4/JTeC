package edu.tum.sse.jtec.instrumentation.testevent.interceptors;

import edu.tum.sse.jtec.instrumentation.testevent.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;

public class JUnit4TestRunStartedInterceptor {
    @Advice.OnMethodEnter
    public static void enter() {
        try {
            TestEventInterceptorUtility.maybeTriggerGlobalSetupCoverageDump();
        } catch (final Exception e) {
            System.err.println("Exception in test run start is: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
