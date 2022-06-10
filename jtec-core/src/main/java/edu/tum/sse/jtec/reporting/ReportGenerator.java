package edu.tum.sse.jtec.reporting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates a JTeC test report based on the logs created from the JTeC instrumentation agent.
 */
public class ReportGenerator {
    private final Path outputDirectory;
    private final boolean isForkingMode;

    public ReportGenerator(final Path outputDirectory, final boolean isForkingMode) {
        this.outputDirectory = outputDirectory;
        this.isForkingMode = isForkingMode;
    }

    private static Map<String, List<TestSuite>> parseTestLogs(final List<Path> testLogs) throws IOException {
        final Map<String, List<TestSuite>> testSuites = new HashMap<>();
        for (final Path testLog : testLogs) {
            parseLogFile(testSuites, testLog);

        }
        return testSuites;
    }

    public TestReport generateReport(final String reportId) throws IOException {
        final long timestamp = System.currentTimeMillis();
        final long totalDuration = 0;
        final List<Path> testLogs = new ArrayList<>();
        final List<Path> sysLogs = new ArrayList<>();
        final List<Path> covLogs = new ArrayList<>();

        Files.walk(outputDirectory, 2)
                .forEach(path -> {
                    if (path.toString().endsWith("test.log")) {
                        testLogs.add(path);
                    } else if (path.toString().endsWith("sys.log")) {
                        sysLogs.add(path);
                    } else if (path.toString().endsWith("cov.log")) {
                        covLogs.add(path);
                    }
                });

        final Map<String, List<TestSuite>> testSuites = parseTestLogs(testLogs);


//        return new TestReport(
//                reportId,
//                timestamp,
//                totalDuration,
//                testSuites
//        );
        return null;
    }

}
