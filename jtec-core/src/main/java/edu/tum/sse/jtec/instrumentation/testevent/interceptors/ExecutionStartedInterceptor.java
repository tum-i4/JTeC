package edu.tum.sse.jtec.instrumentation.testevent.interceptors;

import edu.tum.sse.jtec.instrumentation.testevent.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;
import org.junit.platform.launcher.TestIdentifier;

public class ExecutionStartedInterceptor {

    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final TestIdentifier testIdentifier) {
        if (testIdentifier == null) {
            return;
        }
        try {
            TestEventInterceptorUtility.executionStarted(testIdentifier, TestEventInterceptorUtility.convertTestIdentifierToDescription(testIdentifier));
        } catch (final Exception e) {
            System.err.println("Exception, printedName is: " + testIdentifier);
            e.printStackTrace();
        }
    }
}
