package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.agent.AgentOptions;
import edu.tum.sse.jtec.agent.JTeCAgent;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * This Mojo is used to attach the JTeC agent before tests are executed in a Surefire or Failsafe project.
 * It does so by adding the -javaagent runtime option to each JVM that executes tests.
 * Notably, we do not mess with the `argLine` option, but rather use the
 * <a href="https://maven.apache.org/surefire/maven-surefire-plugin/test-mojo.html#debugForkedProcess">Surefire</a> or
 * <a href="https://maven.apache.org/surefire/maven-failsafe-plugin/integration-test-mojo.html#debugForkedProcess">Failsafe</a> debug option.
 */
@Mojo(name = "jtec", defaultPhase = LifecyclePhase.INITIALIZE, threadSafe = true)
public class JTeCMojo extends AbstractJTeCMojo {

    private static final String SUREFIRE_DEBUG_OPTION = "maven.surefire.debug";
    private static final String FAILSAFE_DEBUG_OPTION = "maven.failsafe.debug";

    /**
     * JTeC options passed to the JTeC agent.
     */
    @Parameter(property = "jtec.opts", readonly = true)
    String agentOpts;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            String preparedAgentOpts = prepareAgentOpts();
            log("Executing JTeC Maven plugin with agentOpts=" + agentOpts + " for project " + project.getName());
            Path agentJar = locateAgentJar();
            Properties properties = project.getProperties();
            for (String property : new String[]{SUREFIRE_DEBUG_OPTION, FAILSAFE_DEBUG_OPTION}) {
                String oldValue = properties.getProperty(property);
                String newValue = String.format("%s-javaagent:%s=%s", (oldValue == null ? "" : oldValue + " "), agentJar.toAbsolutePath(), preparedAgentOpts);
                properties.setProperty(property, newValue);
                log(String.format("Changing Maven property %s to %s.", property, newValue));
            }
        } catch (Exception exception) {
            getLog().error("Failed to find JTeC agent JAR, skipping instrumentation.");
            exception.printStackTrace();
        }
    }

    private String prepareAgentOpts() {
        AgentOptions agentOptions;
        if (agentOpts == null) {
            agentOptions = AgentOptions.DEFAULT_OPTIONS;
        } else {
            agentOptions = AgentOptions.fromString(agentOpts);
        }
        agentOptions.setOutputPath(outputDirectory.toPath());
        return agentOptions.toAgentString();
    }

    private Path locateAgentJar() throws IOException, URISyntaxException {
        URL url = JTeCAgent.class.getResource("/" + JTeCAgent.class.getName().replace('.', '/') + ".class");
        URI jarURL = ((JarURLConnection) url.openConnection()).getJarFileURL().toURI();
        return Paths.get(jarURL);
    }
}
