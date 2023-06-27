package edu.tum.sse.jtec.reporting;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import static edu.tum.sse.jtec.util.JSONUtils.fromJson;

/**
 * Merges an old and a new JTeC report and may replace traces only for passing tests.
 * This class guarantees to output a valid JTeC report otherwise the merging will fail.
 */
public class ReportMerger {
    final Path oldReportFile;
    final Path newReportFile;
    final boolean updateOnlyPassingTests;

    public ReportMerger(Path oldReportFile, Path newReportFile, boolean updateOnlyPassingTests) {
        this.oldReportFile = oldReportFile;
        this.newReportFile = newReportFile;
        this.updateOnlyPassingTests = updateOnlyPassingTests;
    }

    private TestReport mergeReports(TestReport oldReport, TestReport newReport) {
        HashMap<String, TestSuite> testSuites = new HashMap<>();
        for (TestSuite testSuite : oldReport.getTestSuites()) {
            testSuites.put(testSuite.getTestId(), testSuite);
        }
        for (TestSuite testSuite : newReport.getTestSuites()) {
            if (!updateOnlyPassingTests || testSuite.getFailureCount() == 0 || !testSuites.containsKey(testSuite.getTestId())) {
                testSuites.put(testSuite.getTestId(), testSuite);
            }
        }
        return new TestReport(newReport.getReportId(),
                newReport.getTimestamp(),
                -1,
                new ArrayList<>(testSuites
                        .values()));
    }

    public TestReport merge() {
        TestReport oldReport = null;
        TestReport newReport = null;
        Exception thrownException = null;
        try {
            oldReport = fromJson(oldReportFile, TestReport.class);
        } catch (Exception e) {
            thrownException = e;
        }
        try {
            newReport = fromJson(newReportFile, TestReport.class);
        } catch (Exception e) {
            thrownException = e;
        }
        if (oldReport == null && newReport == null) {
            if (thrownException != null) {
                throw new RuntimeException(thrownException);
            } else {
                throw new RuntimeException();
            }
        }
        if (oldReport == null) {
            return newReport;
        }
        if (newReport == null) {
            return oldReport;
        }
        return mergeReports(oldReport, newReport);
    }
}
