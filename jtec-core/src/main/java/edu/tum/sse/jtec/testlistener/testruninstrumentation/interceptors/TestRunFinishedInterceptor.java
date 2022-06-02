package edu.tum.sse.jtec.testlistener.testruninstrumentation.interceptors;

import edu.tum.sse.jtec.testlistener.testruninstrumentation.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;
import org.junit.runner.Result;

public class TestRunFinishedInterceptor {
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final Result result) {
        try {
            TestEventInterceptorUtility.testRunFinished(result.getRunCount(), result.getFailureCount(), result.getIgnoreCount());
        } catch (final Exception e) {
            System.err.println("Exception in test run finished is: " + result);
            e.printStackTrace();
        }
    }
}
