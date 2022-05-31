package edu.tum.sse.jtec.testlistener.testRunInstrumentation;

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

public class TestEventInterceptorUtility {

        public static String testingLogFilePath;
        public static TestRunResult currentTestRunResult = null;

        public static String currentTestSuite = "";
        public static String currentTestCase = "";

        public static boolean inTestSuite = false;

        public static String currentPid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];


        /**
         * We must convert the JUnit5 {@link TestIdentifier} to a JUnit4-compatible description format {@link Description}.
         * This method will return `null` in case the test identifier is neither a test suite nor a test method.
         */
        public static Description convertTestIdentifierToDescription(TestIdentifier testIdentifier) {
            // [engine:junit-vintage/jupiter] containers will not have a parent and are excluded
            if (!testIdentifier.getParentId().isPresent()) {
                return null;
            }

            if (!testIdentifier.getSource().isPresent()) {
                return null;
            }
            TestSource source = testIdentifier.getSource().get();

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
                MethodSource methodSource = (MethodSource) source;
                String className = methodSource.getClassName();
                return Description.createTestDescription(className, testIdentifier.getDisplayName(), testIdentifier.getUniqueId());
            }
            return Description.createSuiteDescription(testIdentifier.getDisplayName(), testIdentifier.getUniqueId());
        }

        public static void executionStarted(TestIdentifier testIdentifier, Description description) {
            if (description != null) {
                if (testIdentifier.isTest()) {
                    testStarted(description);
                } else {
                    setupTestResult(testIdentifier.getUniqueId());
                    testRunStarted(description);
                }
            }
        }

        public static void setupTestResult(String testId) {
            if (currentTestRunResult == null || !(currentTestRunResult.getTestIdentifier().equals(testId))) {
                currentTestRunResult = new TestRunResult(testId);
            }
        }

        public static void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult, Description description) {
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

        public static void testStarted(Description description) {
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

        public static void testRunStarted(Description description) {
            if (inTestSuite) {
                return;
            }
            inTestSuite = true;
            sendMessage(String.format("%d %s %s", System.currentTimeMillis(), currentPid, TestTracingEvent.SUITE_STARTED.name()));
        }

        public static void testRunFinished() {
            testRunFinished(currentTestRunResult.getRunCount(), currentTestRunResult.getFailureCount(), currentTestRunResult.getIgnoreCount());
        }

        public static void testRunFinished(int runCount, int failureCount, int ignoreCount) {
            if (currentTestSuite.equals("")) {
                return;
            }
            sendMessage(String.format("%d %s %s %s %d %d %d", System.currentTimeMillis(), currentPid, TestTracingEvent.SUITE_FINISHED.name(), currentTestSuite, runCount, failureCount, ignoreCount));
            currentTestSuite = "";
            inTestSuite = false;
        }

        public static void sendMessage(String message) {
            try {
                Files.write(Paths.get(testingLogFilePath), (message + "\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.err.println("Exception occurred: " + e.getMessage());
            }
        }

        private static String getTestSuiteName(Description description) {
            String displayName = description.getDisplayName();
            return displayName.substring(displayName.indexOf("(") + 1, displayName.indexOf(")"));
        }

        private static String getTestCaseName(Description description) {
            String displayName = description.getDisplayName();
            return displayName.substring(0, displayName.indexOf("("));
        }

}
