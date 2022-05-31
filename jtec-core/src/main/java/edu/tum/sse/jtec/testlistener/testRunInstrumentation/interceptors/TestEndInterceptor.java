package edu.tum.sse.jtec.testlistener.testRunInstrumentation.interceptors;

import edu.tum.sse.jtec.testlistener.testRunInstrumentation.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;
import org.junit.runner.Description;

public class TestEndInterceptor {
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) Description testDescription) {
        try {
            TestEventInterceptorUtility.testFinished();
        } catch (Exception e) {
            System.err.println("Exception in test end is: " + testDescription);
            e.printStackTrace();
        }
    }
}
