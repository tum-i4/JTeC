package edu.tum.sse.jtec.mojo;

import edu.tum.sse.jtec.agent.AgentOptions;
import edu.tum.sse.jtec.agent.JTeCAgent;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static edu.tum.sse.jtec.util.IOUtils.locateJar;

/**
 * This Mojo is used to attach the JTeC agent before tests are executed in a Surefire or Failsafe project.
 * It does so by adding the -javaagent runtime option to each JVM that executes tests.
 * Notably, we do not mess with the `argLine` option, but rather use the
 * <a href="https://maven.apache.org/surefire/maven-surefire-plugin/test-mojo.html#debugForkedProcess">Surefire</a> or
 * <a href="https://maven.apache.org/surefire/maven-failsafe-plugin/integration-test-mojo.html#debugForkedProcess">Failsafe</a> debug option.
 */
@Mojo(name = "jtec", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES, threadSafe = true)
public class JTeCMojo extends AbstractJTeCMojo {

    public static final char PACKAGE_SEPARATOR = '.';
    private static final String SUREFIRE_DEBUG_OPTION = "maven.surefire.debug";
    private static final String FAILSAFE_DEBUG_OPTION = "maven.failsafe.debug";

    /**
     * JTeC options passed to the JTeC agent.
     */
    @Parameter(property = "jtec.opts", readonly = true)
    String agentOpts;

    /**
     * JTeC option to generate include patterns automatically.
     * Only works reliably if the test and source classes are in the same module.
     */
    @Parameter(property = "jtec.autoinclude", readonly = true, defaultValue = "false")
    Boolean autoinclude;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            log("Executing JTeC Maven plugin with agentOpts=" + agentOpts + " for project " + project.getName());
            final String preparedAgentOpts = prepareAgentOpts();
            final Path agentJar = locateJar(JTeCAgent.class);
            final Properties properties = project.getProperties();
            for (final String property : new String[]{SUREFIRE_DEBUG_OPTION, FAILSAFE_DEBUG_OPTION}) {
                final String oldValue = properties.getProperty(property);
                final String newValue = String.format("%s-javaagent:%s=%s", (oldValue == null ? "" : oldValue + " "), agentJar.toAbsolutePath(), preparedAgentOpts);
                properties.setProperty(property, newValue);
                log(String.format("Changing Maven property %s to %s.", property, newValue));
            }
        } catch (final Exception exception) {
            getLog().error("Failed to find JTeC agent JAR, skipping instrumentation.");
            exception.printStackTrace();
        }
    }

    private String prepareAgentOpts() {
        final AgentOptions agentOptions;
        if (agentOpts == null) {
            agentOptions = AgentOptions.DEFAULT_OPTIONS;
        } else {
            agentOptions = AgentOptions.fromString(agentOpts);
        }
        agentOptions.setOutputPath(outputDirectory.toPath());
        if (autoinclude && Files.exists(Paths.get(project.getBuild().getOutputDirectory()))) {
            String autoincludePatterns = "";
            try {
                autoincludePatterns = buildAutoIncludePattern();
            } catch (final IOException e) {
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

    private String buildAutoIncludePattern() throws IOException {
        final Set<String> includePaths = new HashSet<>();
        final String classDirectory = project.getBuild().getOutputDirectory();
        findAutoincludePatterns(Paths.get(classDirectory), classDirectory, includePaths);

        final String testClassDir = project.getBuild().getTestOutputDirectory();
        findAutoincludePatterns(Paths.get(testClassDir), testClassDir, includePaths);

        final StringBuilder result = new StringBuilder();
        for (final String path : includePaths) {
            result.append(path);
            result.append(AgentOptions.PIPE_REPLACEMENT);
        }
        result.deleteCharAt(result.length() - 1);
        final String includePackages = result.toString().replace(File.separatorChar, PACKAGE_SEPARATOR);
        return "(" + includePackages + ")" + ".*";
    }

    private void findAutoincludePatterns(final Path path, final String classDir, final Set<String> includePaths) throws IOException {
        final List<Path> paths = new ArrayList<>();
        log("Searching for autoinclude Patterns in " + path);
        Files.list(path).forEach(paths::add);
        if (paths.stream().anyMatch(currentPath -> currentPath.toString().endsWith(".class"))) {
            includePaths.add(path.toString().substring(classDir.length() + 1));
            return;
        }
        for (final Path currentPath : paths) {
            if (Files.isDirectory(currentPath)) {
                findAutoincludePatterns(currentPath, classDir, includePaths);
            }
        }
    }

}
