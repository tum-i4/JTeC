package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.reporting.ReportGenerator;
import edu.tum.sse.jtec.reporting.ReportMerger;
import edu.tum.sse.jtec.reporting.TestReport;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static edu.tum.sse.jtec.util.JSONUtils.fromJson;

/**
 * This Mojo is used to update an existing JTeC report with a new one.
 * Will by default only update those test suites which passed in the new report.
 */
@Mojo(name = "update-report", threadSafe = true, aggregator = true)
public class JTeCUpdateReportMojo extends AbstractJTeCReportMojo {

    @Parameter(property = "jtec.onlyPassing", defaultValue = "true")
    Boolean onlyPassing;

    @Parameter(property = "jtec.oldReport", required = true)
    File oldReport;

    @Parameter(property = "jtec.newReport", required = true)
    File newReport;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (!Files.exists(outputDirectory.toPath())) {
                getLog().error("Report generator could not find JTeC output directory: " + outputDirectory.toString());
                return;
            }
            final ReportMerger merger = new ReportMerger(oldReport.toPath(), newReport.toPath(), onlyPassing);
            final TestReport testReport = merger.merge();
            storeTestReport(testReport);
        } catch (final Exception exception) {
            getLog().error("Failed to generate JTeC report in project " + project.getName());
            exception.printStackTrace();
        }
    }

}
