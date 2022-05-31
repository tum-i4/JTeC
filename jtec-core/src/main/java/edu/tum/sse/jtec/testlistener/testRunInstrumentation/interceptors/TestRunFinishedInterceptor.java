package edu.tum.sse.jtec.testlistener.testRunInstrumentation.interceptors;

import edu.tum.sse.jtec.testlistener.testRunInstrumentation.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;
import org.junit.runner.Result;

public class TestRunFinishedInterceptor {
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) Result result) {
        try {
            TestEventInterceptorUtility.testRunFinished(result.getRunCount(), result.getFailureCount(), result.getIgnoreCount());
        } catch (Exception e) {
            System.err.println("Exception in test run finished is: " + result);
            e.printStackTrace();
        }
    }
}
