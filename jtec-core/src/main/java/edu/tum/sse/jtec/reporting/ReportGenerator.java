package edu.tum.sse.jtec.reporting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates a JTeC test report based on the logs created from the JTeC instrumentation agent.
 */
public final class ReportGenerator {

    private static final String TEST_LOG_SUFFIX = "test.log";
    private static final String SYSTEM_LOG_SUFFIX = "sys.log";
    private static final String COVERAGE_LOG_SUFFIX = "cov.log";

    private final Path outputDirectory;
    private final boolean isForkingMode;

    public ReportGenerator(final Path outputDirectory, final boolean isForkingMode) {
        this.outputDirectory = outputDirectory;
        this.isForkingMode = isForkingMode;
    }

    public TestReport aggregateReports(final String reportId, final List<TestReport> testReports) {
        final long timestamp = System.currentTimeMillis();
        List<TestSuite> aggregatedTestSuites = testReports.stream()
                .map(TestReport::getTestSuites)
                .flatMap(List::stream)
                .sorted(Comparator.comparingLong(TestSuite::getStartTimestamp))
                .collect(Collectors.toList());
        final long totalDuration = aggregatedTestSuites.size() == 0 ? 0 : aggregatedTestSuites.get(aggregatedTestSuites.size() - 1).getEndTimestamp() - aggregatedTestSuites.get(0).getStartTimestamp();
        return new TestReport(reportId, timestamp, totalDuration, aggregatedTestSuites);
    }

    public TestReport generateReport(final String reportId) throws IOException {
        final long timestamp = System.currentTimeMillis();
        final List<Path> testLogs = new ArrayList<>();
        final List<Path> sysLogs = new ArrayList<>();
        final List<Path> covLogs = new ArrayList<>();

        Files.walk(outputDirectory, 2)
                .forEach(path -> {
                    if (path.toString().endsWith(TEST_LOG_SUFFIX)) {
                        testLogs.add(path);
                    } else if (path.toString().endsWith(SYSTEM_LOG_SUFFIX)) {
                        sysLogs.add(path);
                    } else if (path.toString().endsWith(COVERAGE_LOG_SUFFIX)) {
                        covLogs.add(path);
                    }
                });

        Map<String, List<TestSuite>> testSuiteMap = parseTestLogs(testLogs);
        parseSystemLogs(sysLogs, testSuiteMap);
        parseCoverageLogs(covLogs, testSuiteMap);

        List<TestSuite> testSuites = testSuiteMap.values().stream()
                .flatMap(List::stream)
                .sorted(Comparator.comparingLong(TestSuite::getStartTimestamp))
                .collect(Collectors.toList());
        final long totalDuration = testSuites.size() == 0 ? 0 : testSuites.get(testSuites.size() - 1).getEndTimestamp() - testSuites.get(0).getStartTimestamp();
        return new TestReport(
                reportId,
                timestamp,
                totalDuration,
                testSuites
        );
    }

    private Map<String, List<TestSuite>> parseTestLogs(final List<Path> testLogs) throws IOException {
        Map<String, List<TestSuite>> testSuites = new HashMap<>();
        TestingLogParser parser = new TestingLogParser(isForkingMode);
        for (final Path testLog : testLogs) {
            Map<String, List<TestSuite>> testSuitesFromFile = parser.parse(testLog);
            testSuitesFromFile.forEach((pid, suites) -> {
                if (!testSuites.containsKey(pid)) {
                    testSuites.put(pid, suites);
                } else {
                    testSuites.get(pid).addAll(suites);
                }
            });
        }
        return testSuites;
    }

    private void parseCoverageLogs(List<Path> covLogs, Map<String, List<TestSuite>> testSuiteMap) throws IOException {
        if (testSuiteMap.size() == 0) return;
        CoverageLogParser parser = new CoverageLogParser();
        for (final Path covLog : covLogs) {
            parser.parse(covLog, testSuiteMap);
        }
    }

    private void parseSystemLogs(List<Path> sysLogs, Map<String, List<TestSuite>> testSuiteMap) throws IOException {
        if (testSuiteMap.size() == 0) return;
        SystemEventLogParser parser = new SystemEventLogParser();
        for (final Path sysLog : sysLogs) {
            parser.parse(sysLog, testSuiteMap);
        }
    }

}
