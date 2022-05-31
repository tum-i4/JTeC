package edu.tum.sse.jtec.testlistener.testRunInstrumentation.interceptors;

import edu.tum.sse.jtec.testlistener.testRunInstrumentation.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;
import org.junit.runner.Description;

public class TestStartInterceptor {
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) Description testDescription) {
        try {
            TestEventInterceptorUtility.testStarted(testDescription);
        } catch (Exception e) {
            System.err.println("Exception in test start is: " + testDescription);
            e.printStackTrace();
        }
    }
}