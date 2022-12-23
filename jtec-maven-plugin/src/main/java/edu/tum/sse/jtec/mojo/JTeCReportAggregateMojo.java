package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.reporting.ReportGenerator;
import edu.tum.sse.jtec.reporting.TestReport;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static edu.tum.sse.jtec.util.JSONUtils.fromJson;

/**
 * This Mojo is used to generate an aggregated JTeC report for a Maven reactor.
 */
@Mojo(name = "report-aggregate", threadSafe = true, aggregator = true)
public class JTeCReportAggregateMojo extends AbstractJTeCReportMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (session.getCurrentProject().isExecutionRoot()) {
                List<Path> reportPaths = session.getAllProjects().stream()
                        .map(mavenProject -> mavenProject.getBasedir().toPath().resolve("target/jtec").resolve(TEST_REPORT_JSON_FILENAME))
                        .filter(Files::exists)
                        .collect(Collectors.toList());
                List<TestReport> testReports = reportPaths.stream()
                        .map(path -> {
                            try {
                                return fromJson(String.join("", Files.readAllLines(path)), TestReport.class);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                ReportGenerator reportGenerator = new ReportGenerator(outputDirectory.toPath(), true);
                TestReport testReport = reportGenerator.aggregateReports(project.getName(), testReports);
                session.setProjects(Collections.emptyList());
                if (!storeTestReport(testReport)) {
                    throw new MojoFailureException("Failed to create aggregated JTeC test report, found only empty reports.");
                }
            }
        } catch (final Exception exception) {
            getLog().error("Failed to aggregate JTeC reports in project " + project.getName());
            exception.printStackTrace();
            throw new MojoFailureException(exception.getMessage());
        }
    }

}
