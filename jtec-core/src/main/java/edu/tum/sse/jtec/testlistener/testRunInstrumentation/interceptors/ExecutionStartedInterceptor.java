package edu.tum.sse.jtec.testlistener.testRunInstrumentation.interceptors;

import edu.tum.sse.jtec.testlistener.testRunInstrumentation.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;
import org.junit.platform.launcher.TestIdentifier;

public class ExecutionStartedInterceptor {

    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) TestIdentifier testIdentifier) {
        if (testIdentifier == null) {
            return;
        }
        try {
            TestEventInterceptorUtility.executionStarted(testIdentifier, TestEventInterceptorUtility.convertTestIdentifierToDescription(testIdentifier));
        } catch (Exception e) {
            System.err.println("Exception, printedName is: " + testIdentifier);
            e.printStackTrace();
        }
    }
}
