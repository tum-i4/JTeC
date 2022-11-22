package edu.tum.sse.jtec.reporting;

import java.util.HashSet;
import java.util.Set;

public class TestSuite {
    private String testId;
    private long duration;
    private long startTimestamp;
    private long endTimestamp;
    private int runCount = 0;
    private int failureCount = 0;
    private int ignoreCount = 0;
    private Set<String> spawnedProcesses = new HashSet<>();  // PIDs
    private Set<String> startedThreads = new HashSet<>();  // Thread names
    private Set<String> connectedSockets = new HashSet<>();  // Address and port
    private Set<String> openedFiles = new HashSet<>();  // File paths
    private Set<String> coveredEntities = new HashSet<>(); // Class- or method-names

    public TestSuite() {
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

    public int getRunCount() {
        return runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public int getIgnoreCount() {
        return ignoreCount;
    }

    public void setIgnoreCount(int ignoreCount) {
        this.ignoreCount = ignoreCount;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Set<String> getSpawnedProcesses() {
        return spawnedProcesses;
    }

    public void setSpawnedProcesses(Set<String> spawnedProcesses) {
        this.spawnedProcesses = spawnedProcesses;
    }

    public Set<String> getStartedThreads() {
        return startedThreads;
    }

    public void setStartedThreads(Set<String> startedThreads) {
        this.startedThreads = startedThreads;
    }

    public Set<String> getConnectedSockets() {
        return connectedSockets;
    }

    public void setConnectedSockets(Set<String> connectedSockets) {
        this.connectedSockets = connectedSockets;
    }

    public Set<String> getOpenedFiles() {
        return openedFiles;
    }

    public void setOpenedFiles(Set<String> openedFiles) {
        this.openedFiles = openedFiles;
    }

    public Set<String> getCoveredEntities() {
        return coveredEntities;
    }

    public void setCoveredEntities(Set<String> coveredEntities) {
        this.coveredEntities = coveredEntities;
    }
}
