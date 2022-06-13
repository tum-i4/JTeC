package edu.tum.sse.jtec.reporting;

import java.util.List;

public final class TestReport {
    private String reportId;
    private long timestamp;
    private long totalDuration;
    private List<TestSuite> testSuites;

    public TestReport(String reportId, long timestamp, long totalDuration, List<TestSuite> testSuites) {
        this.reportId = reportId;
        this.timestamp = timestamp;
        this.totalDuration = totalDuration;
        this.testSuites = testSuites;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(long totalDuration) {
        this.totalDuration = totalDuration;
    }

    public List<TestSuite> getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(List<TestSuite> testSuites) {
        this.testSuites = testSuites;
    }
}
