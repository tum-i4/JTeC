package edu.tum.sse.jtec.reporting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static edu.tum.sse.jtec.util.JSONUtils.fromJson;


public final class CoverageLogParser {
    public CoverageLogParser() {
    }

    private Optional<TestSuite> findMatchingTestSuite(long timestamp, List<TestSuite> testSuites) {
        return testSuites.stream()
                .filter(testSuite -> testSuite.getStartTimestamp() <= timestamp && testSuite.getEndTimestamp() >= timestamp)
                .findFirst();
    }

    public void parse(Path covLog, Map<String, List<TestSuite>> testSuiteMap) throws IOException {
        String jsonString = String.join("", Files.readAllLines(covLog));
        final String[] parts = covLog.getFileName().toString().split("_");
        final String pid = parts[0];
        final long startTimestamp = Long.parseLong(parts[1]);
        Map<String, Set<String>> coverageMap = fromJson(jsonString);
        for (Map.Entry<String, Set<String>> entry : coverageMap.entrySet()) {
            // For coverage log where key is PID.
            if (testSuiteMap.containsKey(entry.getKey())) {
                findMatchingTestSuite(startTimestamp, testSuiteMap.get(entry.getKey()))
                        .ifPresent(testSuite -> testSuite.getCoveredEntities().addAll(entry.getValue()));
            } else {
                // For coverage log where key is test suite identifier.
                if (testSuiteMap.containsKey(pid)) {
                    for (TestSuite testSuite : testSuiteMap.get(pid)) {
                        if (testSuite.getTestId().equals(entry.getKey())) {
                            testSuite.getCoveredEntities().addAll(entry.getValue());
                            break;
                        }
                    }
                    // TODO: If in non-forked mode, we could add all global test setup and
                    //  global test teardown coverage to all test suites here.
                }
            }
        }
    }
}
