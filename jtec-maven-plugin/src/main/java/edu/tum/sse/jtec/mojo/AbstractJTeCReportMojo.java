package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.reporting.TestReport;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static edu.tum.sse.jtec.util.IOUtils.createFileAndEnclosingDir;
import static edu.tum.sse.jtec.util.IOUtils.writeToFile;
import static edu.tum.sse.jtec.util.JSONUtils.toJson;
import static edu.tum.sse.jtec.util.LCOVUtils.toLcov;

public abstract class AbstractJTeCReportMojo extends AbstractJTeCMojo {

    final static String TEST_REPORT_JSON_FILENAME = "test-report.json";
    final static String TEST_REPORT_LCOV_FILENAME = "test-report.info";

    /**
     * JTeC option to enable generating an LCOV file for the test report.
     */
    @Parameter(property = "jtec.lcov", readonly = true, defaultValue = "false")
    boolean lcovEnabled;

    boolean storeTestReport(TestReport testReport) throws IOException {
        Path jsonFile = outputDirectory.toPath().resolve(TEST_REPORT_JSON_FILENAME);
        Path baseDirectory = project.getBasedir().toPath();
        Files.deleteIfExists(jsonFile);
        if (testReport.getTestSuites().size() == 0) {
            getLog().warn("No Test Suites found during report generation.");
            return false;
        }
        final String jsonTestReport = toJson(testReport);
        createFileAndEnclosingDir(jsonFile);
        writeToFile(jsonFile, jsonTestReport, false, StandardOpenOption.TRUNCATE_EXISTING);
        if (lcovEnabled) {
            Path lcovFile = outputDirectory.toPath().resolve(TEST_REPORT_LCOV_FILENAME);
            Files.deleteIfExists(lcovFile);
            final String lcovTestReport = toLcov(testReport, baseDirectory);
            createFileAndEnclosingDir(lcovFile);
            writeToFile(lcovFile, lcovTestReport, false, StandardOpenOption.TRUNCATE_EXISTING);
        }
        return true;
    }
}
