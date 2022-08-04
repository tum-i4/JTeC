package edu.tum.sse.jtec.instrumentation.testevent.interceptors;

import edu.tum.sse.jtec.instrumentation.testevent.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;

public class JUnit4TestFailedInterceptor {

    @Advice.OnMethodEnter
    public static void enter() {
        try {
            TestEventInterceptorUtility.incrementFailureCount();
        } catch (final Exception e) {
            System.err.println("Exception in increaseIgnoreCount: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
