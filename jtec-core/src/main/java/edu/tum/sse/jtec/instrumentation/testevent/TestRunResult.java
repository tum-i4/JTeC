package edu.tum.sse.jtec.instrumentation.testevent;

public class TestRunResult {
    private final String testSuiteName;
    private int runCount = 0;
    private int failureCount = 0;
    private int ignoreCount = 0;

    public TestRunResult(final String testSuiteName) {
        this.testSuiteName = testSuiteName;
    }

    public String getTestSuiteName() {
        return testSuiteName;
    }

    public int getRunCount() {
        return runCount;
    }

    public void incrementRunCount() {
        this.runCount++;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void incrementFailureCount() {
        this.failureCount++;
    }

    public int getIgnoreCount() {
        return ignoreCount;
    }

    public void incrementIgnoreCount() {
        this.ignoreCount++;
    }
}

