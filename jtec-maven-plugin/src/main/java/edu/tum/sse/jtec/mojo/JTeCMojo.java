package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.agent.AgentOptions;
import edu.tum.sse.jtec.agent.JTeCAgent;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.nio.file.Path;
import java.util.Properties;

import static edu.tum.sse.jtec.util.IOUtils.locateJar;

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

    @Parameter(property = "jtec.argLine", readonly = true)
    String argLine;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            log("Executing JTeC Maven plugin with agentOpts=" + agentOpts + " for project " + project.getName());
            String preparedAgentOpts = prepareAgentOpts();
            Path agentJar = locateJar(JTeCAgent.class);
            Properties properties = project.getProperties();
            String[] propertiesToChanges;
            if (argLine != null && !argLine.isEmpty()) {
                propertiesToChanges = new String[]{argLine};
            } else {
                propertiesToChanges = new String[]{SUREFIRE_DEBUG_OPTION, FAILSAFE_DEBUG_OPTION};
            }
            for (String property : propertiesToChanges) {
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
        String agentString = agentOptions.toAgentString();
        // Fix for Win32 where a pipe character in a command line sometimes breaks process execution.
        agentString = agentString.replace("|", AgentOptions.PIPE_REPLACEMENT);
        return agentString;
    }

}
