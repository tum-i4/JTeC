package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.reporting.TestReport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static edu.tum.sse.jtec.util.IOUtils.createFileAndEnclosingDir;
import static edu.tum.sse.jtec.util.IOUtils.writeToFile;
import static edu.tum.sse.jtec.util.JSONUtils.toJson;

public abstract class AbstractJTeCReportMojo extends AbstractJTeCMojo {

    final static String TEST_REPORT_FILENAME = "test-report.json";

    boolean storeTestReport(TestReport testReport) throws IOException {
        Path jsonFile = outputDirectory.toPath().resolve(TEST_REPORT_FILENAME);
        Files.deleteIfExists(jsonFile);
        if (testReport.getTestSuites().size() == 0) {
            getLog().warn("No Test Suites found during report generation.");
            return false;
        }
        final String jsonTestReport = toJson(testReport);
        createFileAndEnclosingDir(jsonFile);
        writeToFile(jsonFile, jsonTestReport, false, StandardOpenOption.TRUNCATE_EXISTING);
        return true;
    }
}
