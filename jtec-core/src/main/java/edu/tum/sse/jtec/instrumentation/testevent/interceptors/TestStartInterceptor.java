package edu.tum.sse.jtec.instrumentation.testevent.interceptors;

import edu.tum.sse.jtec.instrumentation.testevent.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;
import org.junit.runner.Description;

public class TestStartInterceptor {
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final Description testDescription) {
        try {
            TestEventInterceptorUtility.testStarted(testDescription);
        } catch (final Exception e) {
            System.err.println("Exception in test start is: " + testDescription);
            e.printStackTrace();
        }
    }
}
