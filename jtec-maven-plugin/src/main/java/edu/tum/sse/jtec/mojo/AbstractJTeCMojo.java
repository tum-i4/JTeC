package edu.tum.sse.jtec.mojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

public abstract class AbstractJTeCMojo extends AbstractMojo {

    /**
     * Enable debug output.
     */
    @Parameter(property = "jtec.debug", readonly = true, defaultValue = "false")
    boolean debug;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    /**
     * Output directory for generated JTeC artifacts.
     */
    @Parameter(property = "jtec.output", defaultValue = "${project.build.directory}/jtec")
    File outputDirectory;

    @Parameter(defaultValue = "${session}")
    MavenSession session;

    void log(String message) {
        if (debug) {
            getLog().warn(message);
        } else {
            getLog().debug(message);
        }
    }
}
