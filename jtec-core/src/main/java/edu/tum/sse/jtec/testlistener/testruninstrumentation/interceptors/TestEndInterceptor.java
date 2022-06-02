package edu.tum.sse.jtec.testlistener.testruninstrumentation.interceptors;

import edu.tum.sse.jtec.testlistener.testruninstrumentation.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;
import org.junit.runner.Description;

public class TestEndInterceptor {
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final Description testDescription) {
        try {
            TestEventInterceptorUtility.testFinished();
        } catch (final Exception e) {
            System.err.println("Exception in test end is: " + testDescription);
            e.printStackTrace();
        }
    }
}
