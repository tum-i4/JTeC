package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.reporting.ReportGenerator;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * This Mojo is used to generate a JTeC report for a Maven project.
 */
@Mojo(name = "jtec-report", defaultPhase = LifecyclePhase.VERIFY)
public class JTeCReportMojo extends AbstractJTeCMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            final ReportGenerator generator = new ReportGenerator(outputDirectory.toPath(), true);
            log("Hello from report mojo!");
        } catch (final Exception exception) {

        }
    }

}
