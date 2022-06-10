package edu.tum.sse.jtec.reporting;

import java.util.List;

public final class TestSuite {
    private String testId;
    private long duration;
    private long startTimestamp;
    private long endTimestamp;
    private int passed = 0;
    private int failed = 0;
    private int skipped = 0;
    private List<String> spawnedProcesses;  // PIDs
    private List<String> startedThreads;  // Thread names
    private List<String> connectedSockets;  // Address and port
    private List<String> openedFiles;  // File paths
    private List<String> coveredEntities; // Class- or method-names

    public TestSuite(String testId, long duration, long timestamp, long endTimestamp, int passed, int failed, int skipped, List<String> spawnedProcesses, List<String> startedThreads, List<String> connectedSockets, List<String> openedFiles, List<String> coveredEntities) {
        this.testId = testId;
        this.duration = duration;
        this.startTimestamp = timestamp;
        this.endTimestamp = endTimestamp;
        this.passed = passed;
        this.failed = failed;
        this.skipped = skipped;
        this.spawnedProcesses = spawnedProcesses;
        this.startedThreads = startedThreads;
        this.connectedSockets = connectedSockets;
        this.openedFiles = openedFiles;
        this.coveredEntities = coveredEntities;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

    public List<String> getSpawnedProcesses() {
        return spawnedProcesses;
    }

    public void setSpawnedProcesses(List<String> spawnedProcesses) {
        this.spawnedProcesses = spawnedProcesses;
    }

    public List<String> getStartedThreads() {
        return startedThreads;
    }

    public void setStartedThreads(List<String> startedThreads) {
        this.startedThreads = startedThreads;
    }

    public List<String> getConnectedSockets() {
        return connectedSockets;
    }

    public void setConnectedSockets(List<String> connectedSockets) {
        this.connectedSockets = connectedSockets;
    }

    public List<String> getOpenedFiles() {
        return openedFiles;
    }

    public void setOpenedFiles(List<String> openedFiles) {
        this.openedFiles = openedFiles;
    }

    public List<String> getCoveredEntities() {
        return coveredEntities;
    }

    public void setCoveredEntities(List<String> coveredEntities) {
        this.coveredEntities = coveredEntities;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }
}
