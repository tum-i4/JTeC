package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.reporting.TestReport;

import java.io.File;
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

    boolean storeTestReport(TestReport testReport) throws IOException {
        Path jsonFile = outputDirectory.toPath().resolve(TEST_REPORT_JSON_FILENAME);
        Path lcovFile = outputDirectory.toPath().resolve(TEST_REPORT_LCOV_FILENAME);
        File baseDir = project.getBasedir();
        Files.deleteIfExists(jsonFile);
        Files.deleteIfExists(lcovFile);
        if (testReport.getTestSuites().size() == 0) {
            getLog().warn("No Test Suites found during report generation.");
            return false;
        }
        final String jsonTestReport = toJson(testReport);
        createFileAndEnclosingDir(jsonFile);
        writeToFile(jsonFile, jsonTestReport, false, StandardOpenOption.TRUNCATE_EXISTING);
        final String lcovTestReport = toLcov(testReport, baseDir);
        createFileAndEnclosingDir(lcovFile);
        writeToFile(lcovFile, lcovTestReport, false, StandardOpenOption.TRUNCATE_EXISTING);
        return true;
    }
}
