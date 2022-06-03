package edu.tum.sse.jtec.instrumentation.testevent;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.runner.Description;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.getCurrentPid;

public class TestEventInterceptorUtility {

    public static String testingLogFilePath;
    public static TestRunResult currentTestRunResult = null;

    public static String currentTestSuite = "";
    public static String currentTestCase = "";

    public static boolean inTestSuite = false;

    public static String currentPid = getCurrentPid();


    /**
     * We must convert the JUnit5 {@link TestIdentifier} to a JUnit4-compatible description format {@link Description}.
     * This method will return `null` in case the test identifier is neither a test suite nor a test method.
     */
    public static Description convertTestIdentifierToDescription(final TestIdentifier testIdentifier) {
        // [engine:junit-vintage/jupiter] containers will not have a parent and are excluded
        if (!testIdentifier.getParentId().isPresent()) {
            return null;
        }

        if (!testIdentifier.getSource().isPresent()) {
            return null;
        }
        final TestSource source = testIdentifier.getSource().get();

        // we only count containers as test suites that are classes
        // parameterized test methods are excluded by returning null here
        if (!(testIdentifier.isTest()) && !(source instanceof ClassSource)) {
            return null;
        }

        // a test that does not have a method source is ignored
        if (testIdentifier.isTest() && !(source instanceof MethodSource)) {
            return null;
        }

        if (testIdentifier.isTest()) {
            final MethodSource methodSource = (MethodSource) source;
            final String className = methodSource.getClassName();
            return Description.createTestDescription(className, testIdentifier.getDisplayName(), testIdentifier.getUniqueId());
        }
        return Description.createSuiteDescription(testIdentifier.getDisplayName(), testIdentifier.getUniqueId());
    }

    public static void executionStarted(final TestIdentifier testIdentifier, final Description description) {
        if (description != null) {
            if (testIdentifier.isTest()) {
                testStarted(description);
            } else {
                setupTestResult(testIdentifier.getUniqueId());
                testRunStarted(description);
            }
        }
    }

    public static void setupTestResult(final String testId) {
        if (currentTestRunResult == null || !(currentTestRunResult.getTestIdentifier().equals(testId))) {
            currentTestRunResult = new TestRunResult(testId);
        }
    }

    public static void executionFinished(final TestIdentifier testIdentifier, final TestExecutionResult testExecutionResult, final Description description) {
        if (description != null) {
            if (testIdentifier.isTest()) {
                testFinished();
                incrementRunCount();
                switch (testExecutionResult.getStatus()) {
                    case FAILED:
                        incrementFailureCount();
                        break;
                    case ABORTED:
                        incrementIgnoreCount();
                        break;
                }
            } else {
                testRunFinished();
            }
        }
    }

    public static void incrementFailureCount() {
        currentTestRunResult.incrementFailureCount();
    }

    public static void incrementIgnoreCount() {
        currentTestRunResult.incrementIgnoreCount();
    }

    public static void incrementRunCount() {
        currentTestRunResult.incrementRunCount();
    }

    public static void testStarted(final Description description) {
        if (currentTestCase.equals(getTestCaseName(description))) {
            return;
        }
        currentTestCase = getTestCaseName(description);
        currentTestSuite = getTestSuiteName(description);
        sendMessage(String.format("%d %s %s %s %s", System.currentTimeMillis(), currentPid, TestTracingEvent.TEST_STARTED.name(), currentTestSuite, currentTestCase));
    }

    public static void testFinished() {
        if (currentTestCase.equals("")) {
            return;
        }
        sendMessage(String.format("%d %s %s %s %s", System.currentTimeMillis(), currentPid, TestTracingEvent.TEST_FINISHED.name(), currentTestSuite, currentTestCase));
        currentTestCase = "";
    }

    public static void testRunStarted(final Description description) {
        if (inTestSuite) {
            return;
        }
        inTestSuite = true;
        sendMessage(String.format("%d %s %s", System.currentTimeMillis(), currentPid, TestTracingEvent.SUITE_STARTED.name()));
    }

    public static void testRunFinished() {
        testRunFinished(currentTestRunResult.getRunCount(), currentTestRunResult.getFailureCount(), currentTestRunResult.getIgnoreCount());
    }

    public static void testRunFinished(final int runCount, final int failureCount, final int ignoreCount) {
        if (currentTestSuite.equals("")) {
            return;
        }
        sendMessage(String.format("%d %s %s %s %d %d %d", System.currentTimeMillis(), currentPid, TestTracingEvent.SUITE_FINISHED.name(), currentTestSuite, runCount, failureCount, ignoreCount));
        currentTestSuite = "";
        inTestSuite = false;
    }

    public static void sendMessage(final String message) {
        try {
            Files.write(Paths.get(testingLogFilePath), (message + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (final IOException e) {
            System.err.println("Exception occurred: " + e.getMessage());
        }
    }

    private static String getTestSuiteName(final Description description) {
        final String displayName = description.getDisplayName();
        return displayName.substring(displayName.indexOf("(") + 1, displayName.indexOf(")"));
    }

    private static String getTestCaseName(final Description description) {
        final String displayName = description.getDisplayName();
        return displayName.substring(0, displayName.indexOf("("));
    }

}
