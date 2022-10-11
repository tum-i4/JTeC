package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.agent.AgentOptions;
import edu.tum.sse.jtec.agent.JTeCAgent;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
    private static final String META_INF = "META-INF";


    /**
     * JTeC options passed to the JTeC agent.
     */
    @Parameter(property = "jtec.opts", readonly = true)
    String agentOpts;

    @Parameter(property = "jtec.autoinclude", readonly = true)
    Boolean autoinclude;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            log("Executing JTeC Maven plugin with agentOpts=" + agentOpts + " for project " + project.getName());
            String preparedAgentOpts = prepareAgentOpts();
            Path agentJar = locateJar(JTeCAgent.class);
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
        if (autoinclude) {
            String autoincludePatterns = "";
            try {
                autoincludePatterns = findAutoincludePatterns(Paths.get(project.getBuild().getSourceDirectory()));
                autoincludePatterns = "(" + autoincludePatterns + ")" + ".*";
            } catch (IOException e) {
                getLog().warn("No Source dir found!", e);
            }
            if (!autoincludePatterns.isEmpty()) {
                agentOptions.setCoverageIncludes(autoincludePatterns);
            }
        }
        String agentString = agentOptions.toAgentString();
        // Fix for Win32 where a pipe character in a command line sometimes breaks process execution.
        agentString = agentString.replace("|", AgentOptions.PIPE_REPLACEMENT);
        return agentString;
    }

    private String findAutoincludePatterns(Path path) throws IOException {
        List<Path> paths = new ArrayList<>();
        log("Searching for autoinclude Patterns in " + path);
        Files.list(path).forEach(filePath -> {
            if (!filePath.getFileName().endsWith(META_INF)) {
                paths.add(filePath);
            }
        });
        if (paths.stream().anyMatch(currentPath -> !Files.isDirectory(currentPath))) {
            return path.toString().substring(1);
        }
        StringBuilder result = new StringBuilder();
        for (Path currentPath : paths) {
            result.append(findAutoincludePatterns(currentPath));
            result.append(';');
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString().replace('/', '.');
    }

}
