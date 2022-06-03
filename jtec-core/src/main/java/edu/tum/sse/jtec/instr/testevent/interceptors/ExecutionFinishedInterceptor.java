package edu.tum.sse.jtec.instr.testevent.interceptors;

import edu.tum.sse.jtec.instr.testevent.TestEventInterceptorUtility;
import net.bytebuddy.asm.Advice;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestIdentifier;

public class ExecutionFinishedInterceptor {

    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final TestIdentifier testIdentifier, @Advice.Argument(1) final TestExecutionResult executionResult) {
        if (testIdentifier == null) {
            return;
        }
        try {
            TestEventInterceptorUtility.executionFinished(testIdentifier, executionResult, TestEventInterceptorUtility.convertTestIdentifierToDescription(testIdentifier));
        } catch (final Exception e) {
            System.err.println("Exception, testIdentifier is: " + testIdentifier);
            e.printStackTrace();
        }
    }

}
