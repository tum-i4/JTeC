package edu.tum.sse.jtec.testlistener.testRunInstrumentation.interceptors;

import edu.tum.sse.jtec.testlistener.testRunInstrumentation.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;
import org.junit.runner.Description;

public class TestRunStartedInterceptor {

    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) Description testDescription) {
        if (testDescription == null) {
            return;
        }
        try {
            TestEventInterceptorUtility.testRunStarted(testDescription);
        } catch (Exception e) {
            System.err.println("Exception in test run started is: " + testDescription);
            e.printStackTrace();
        }
    }
}
