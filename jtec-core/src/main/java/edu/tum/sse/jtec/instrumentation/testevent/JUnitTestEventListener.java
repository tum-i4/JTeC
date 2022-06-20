package edu.tum.sse.jtec.instrumentation.testevent;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

@RunListener.ThreadSafe
public class JUnitTestEventListener extends RunListener implements TestExecutionListener {

    @Override
    public void executionStarted(final TestIdentifier testIdentifier) {
        if (testIdentifier == null) {
            return;
        }
        TestEventInterceptorUtility.executionStarted(testIdentifier, TestEventInterceptorUtility.convertTestIdentifierToDescription(testIdentifier));
    }

    @Override
    public void executionFinished(final TestIdentifier testIdentifier, final TestExecutionResult executionResult) {
        if (testIdentifier == null) {
            return;
        }
        TestEventInterceptorUtility.executionFinished(testIdentifier, executionResult, TestEventInterceptorUtility.convertTestIdentifierToDescription(testIdentifier));
    }

    @Override
    public void testRunStarted(final Description testDescription) throws Exception {
        if (testDescription == null) {
            return;
        }
        TestEventInterceptorUtility.testRunStarted(testDescription);
    }

    @Override
    public void testRunFinished(final Result result) throws Exception {
        TestEventInterceptorUtility.testRunFinished(result.getRunCount(), result.getFailureCount(), result.getIgnoreCount());
    }

    @Override
    public void testStarted(final Description testDescription) throws Exception {
        TestEventInterceptorUtility.testStarted(testDescription);
    }

    @Override
    public void testFinished(final Description testDescription) throws Exception {
        TestEventInterceptorUtility.testFinished();
    }
}
