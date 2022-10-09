package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.reporting.ReportGenerator;
import edu.tum.sse.jtec.reporting.TestReport;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.nio.file.*;

/**
 * This Mojo is used to generate a JTeC report for a Maven project.
 */
@Mojo(name = "report", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class JTeCReportMojo extends AbstractJTeCReportMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            if (!Files.exists(outputDirectory.toPath())) {
                return;
            }
            final ReportGenerator generator = new ReportGenerator(outputDirectory.toPath(), true);
            final TestReport testReport = generator.generateReport(project.getName());
            storeTestReport(testReport);
        } catch (final Exception exception) {
            getLog().error("Failed to generate JTeC report in project " + project.getName());
            exception.printStackTrace();
        }
    }

}
