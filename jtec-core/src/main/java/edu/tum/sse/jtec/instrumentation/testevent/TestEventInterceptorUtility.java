package edu.tum.sse.jtec.instrumentation.testevent;

import edu.tum.sse.jtec.instrumentation.coverage.CoverageDumpStrategy;
import edu.tum.sse.jtec.instrumentation.coverage.GlobalCoverageMonitor;
import edu.tum.sse.jtec.util.ProcessUtils;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.runner.Description;

import java.io.IOException;
import java.nio.file.Paths;

import static edu.tum.sse.jtec.util.IOUtils.appendToFile;

public class TestEventInterceptorUtility {

    public static String testingLogFilePath;
    public static boolean testEventInstrumentation = true;

    public static TestRunResult currentTestRunResult = null;

    public static String currentTestSuite = "";
    public static String currentTestCase = "";

    public static boolean inTestSuite = false;
    public static boolean hasTestingStarted = false;

    public static String currentPid = ProcessUtils.getCurrentPid();


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
            } else if (testIdentifier.isContainer() && testIdentifier.getSource().isPresent()) {
                maybeTriggerGlobalSetupCoverageDump();
            }
        }
    }

    public static void maybeTriggerGlobalSetupCoverageDump() {
        if (!hasTestingStarted) {
            // Trigger dump of global test setup coverage, before first test suite executes its test setup code.
            hasTestingStarted = true;
            maybeTriggerCoverageDump(CoverageDumpStrategy.TestPhaseDump.GLOBAL_SETUP.toString());
        }
    }

    public static void setupTestRunResult(final String testSuiteName) {
        if (currentTestRunResult == null || !(currentTestRunResult.getTestSuiteName().equals(testSuiteName))) {
            currentTestRunResult = new TestRunResult(testSuiteName);
        }
    }

    public static void executionFinished(final TestIdentifier testIdentifier, final TestExecutionResult testExecutionResult, final Description description) {
        if (description != null) {
            if (testIdentifier.isTest() && !currentTestCase.isEmpty()) {
                testFinished();
                switch (testExecutionResult.getStatus()) {
                    case FAILED:
                        incrementFailureCount();
                        break;
                    case ABORTED:
                        incrementIgnoreCount();
                        break;
                }
            } else if (inTestSuite) {
                testSuiteFinished();
            }
        }
    }

    private static void maybeTriggerCoverageDump(final String dumpId) {
        if (GlobalCoverageMonitor.isMonitoringCoverage() && !CoverageDumpStrategy.getInstance().isForked()) {
            GlobalCoverageMonitor.get().registerDump(dumpId);
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
        final String testName = getTestCaseName(description);
        if (currentTestCase.equals(testName)) {
            return;
        }
        currentTestCase = testName;

        final String suiteName = getTestSuiteName(description);
        if (!currentTestSuite.equals(suiteName)) {
            if (!currentTestSuite.isEmpty()) {
                testSuiteFinished();
            }
            setupTestRunResult(suiteName);
            testSuiteStarted();
            currentTestSuite = suiteName;
        }
        sendMessage(String.format("%d %s %s %s %s", System.currentTimeMillis(), currentPid, TestTracingEvent.TEST_STARTED.name(), currentTestSuite, currentTestCase));
    }

    public static void testFinished() {
        if (currentTestCase.isEmpty()) {
            return;
        }
        incrementRunCount();
        sendMessage(String.format("%d %s %s %s %s", System.currentTimeMillis(), currentPid, TestTracingEvent.TEST_FINISHED.name(), currentTestSuite, currentTestCase));
        currentTestCase = "";
    }

    public static void testSuiteStarted() {
        if (inTestSuite) {
            return;
        }
        inTestSuite = true;
        sendMessage(String.format("%d %s %s", System.currentTimeMillis(), currentPid, TestTracingEvent.SUITE_STARTED.name()));
    }

    public static void testSuiteFinished() {
        testSuiteFinished(currentTestRunResult.getRunCount(), currentTestRunResult.getFailureCount(), currentTestRunResult.getIgnoreCount());
    }

    public static void testSuiteFinished(final int runCount, final int failureCount, final int ignoreCount) {
        if (currentTestSuite.isEmpty()) {
            return;
        }
        sendMessage(String.format("%d %s %s %s %d %d %d", System.currentTimeMillis(), currentPid, TestTracingEvent.SUITE_FINISHED.name(), currentTestSuite, runCount, failureCount, ignoreCount));
        maybeTriggerCoverageDump(currentTestSuite);
        currentTestSuite = "";
        inTestSuite = false;
    }

    public static void sendMessage(final String message) {
        try {
            // No need to lock here, as we have one testing log per process, no two processes can interfere.
            appendToFile(Paths.get(testingLogFilePath), message + "\n", false);
        } catch (final IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static String getTestSuiteName(final Description description) {
        final String displayName = description.getDisplayName();
        return displayName.substring(displayName.lastIndexOf("(") + 1, displayName.lastIndexOf(")"));
    }

    private static String getTestCaseName(final Description description) {
        final String displayName = description.getDisplayName();
        return displayName.substring(0, displayName.indexOf("("));
    }

}
