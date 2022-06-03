package edu.tum.sse.jtec.instrumentation.test.interceptors;

import edu.tum.sse.jtec.instrumentation.test.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;
import org.junit.runner.Description;

public class TestRunStartedInterceptor {

    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final Description testDescription) {
        if (testDescription == null) {
            return;
        }
        try {
            TestEventInterceptorUtility.testRunStarted(testDescription);
        } catch (final Exception e) {
            System.err.println("Exception in test run started is: " + testDescription);
            e.printStackTrace();
        }
    }
}
