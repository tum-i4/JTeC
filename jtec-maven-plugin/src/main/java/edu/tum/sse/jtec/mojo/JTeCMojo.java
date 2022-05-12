package edu.tum.sse.jtec.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.Properties;

@Mojo(name = "jtec", defaultPhase = LifecyclePhase.PROCESS_TEST_SOURCES)
public class JTeCMojo extends AbstractMojo {

    private static final String SUREFIRE_ARG_LINE_PARAMETER = "argLine";
    private static final String SUREFIRE_PLUGIN_KEY = "org.apache.maven.plugins:maven-surefire-plugin";

    /**
     * A new argLine to append to Maven Surefire plugin in multi-module Maven projects.
     */
    @Parameter(property = "jtec.argLine", readonly = true, required = true)
    String argLine;

    /**
     * The current project.
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Properties properties = project.getProperties();
        String oldValue = properties.getProperty(SUREFIRE_ARG_LINE_PARAMETER);
        String newValue = argLine + " " + (oldValue == null ? "" : oldValue);
        properties.setProperty(SUREFIRE_ARG_LINE_PARAMETER, newValue);
        getLog().info(String.format("Changing argLine in properties to %s.", newValue));
    }
}
