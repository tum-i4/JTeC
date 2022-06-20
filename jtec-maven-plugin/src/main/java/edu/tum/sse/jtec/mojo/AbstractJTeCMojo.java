package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.agent.JTeCAgent;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            getLog().info(message);
        }
    }
}
