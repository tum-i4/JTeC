package edu.tum.sse.jtec.reporting;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates a JTeC test report based on the logs created from the JTeC instrumentation agent.
 */
public class ReportGenerator {
    private final Path outputDirectory;

    public ReportGenerator(Path outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public TestReport generateReport(String reportId) throws IOException {
        long timestamp = System.currentTimeMillis();
        long totalDuration = 0;
        List<Path> testLogs = new ArrayList<>();
        List<Path> sysLogs = new ArrayList<>();
        List<Path> covLogs = new ArrayList<>();

        Files.walk(outputDirectory, 1)
                .forEach(path -> {
                    if (path.endsWith("test.log")) {
                        testLogs.add(path);
                    } else if (path.endsWith("sys.log")) {
                        sysLogs.add(path);
                    } else if (path.endsWith("cov.log")) {
                        covLogs.add(path);
                    }
                });

        List<TestSuite> testSuites = parseTestLogs(testLogs);


        return new TestReport(
                reportId,
                timestamp,
                totalDuration,
                testSuites
        );
    }

    private List<TestSuite> parseTestLogs(List<Path> testLogs) {
        List<TestSuite> testSuites = new ArrayList<>();
        for (Path testLog : testLogs) {

        }
        return testSuites;
    }

}
