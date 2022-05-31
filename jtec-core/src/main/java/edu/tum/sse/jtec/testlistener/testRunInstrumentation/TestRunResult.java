package edu.tum.sse.jtec.testlistener.testRunInstrumentation;

public class TestRunResult {
    private final String testId;
    private int runCount = 0;
    private int failureCount = 0;
    private int ignoreCount = 0;

    public TestRunResult(final String testId) {
        this.testId = testId;
    }

    public String getTestIdentifier() {
        return testId;
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

