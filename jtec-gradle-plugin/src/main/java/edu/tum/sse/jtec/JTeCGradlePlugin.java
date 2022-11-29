package edu.tum.sse.jtec;

import edu.tum.sse.jtec.reporting.ReportGenerator;
import edu.tum.sse.jtec.reporting.TestReport;
import org.gradle.api.Project;
import org.gradle.api.Plugin;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency;
import org.gradle.api.tasks.testing.Test;
import edu.tum.sse.jtec.agent.AgentOptions;
import edu.tum.sse.jtec.agent.JTeCAgent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;
import static edu.tum.sse.jtec.util.IOUtils.*;
import static edu.tum.sse.jtec.util.JSONUtils.toJson;

public class JTeCGradlePlugin implements Plugin<Project> {

    private static final String JTEC = "jtec";
    private static final String JTEC_GROUP = "edu.tum.sse";
    private static final String JTEC_NAME = "jtec-agent";
    File outputDirectory;

    public void apply(Project project) {
        project.getTasks().register("saveReport", task -> {
            task.doLast(s -> saveReport(project));
        });

        this.outputDirectory = new File(project.getBuildDir() + "/jtec");

        try {
            System.out.println("Executing JTeC Gradle plugin for project " + project.getName());
            System.out.println("Out dir: " + this.outputDirectory);
            final String preparedAgentOpts = prepareAgentOpts(project);

            JTeCExtension extension = project.getExtensions().create(JTEC, JTeCExtension.class);
            project.getRepositories().add(project.getRepositories().mavenLocal());
            Configuration agentConfig = project.getConfigurations().detachedConfiguration(
                    new DefaultExternalModuleDependency(JTEC_GROUP, JTEC_NAME, extension.getVersion()));
            final String agentPath = agentConfig.getFiles().iterator().next().getAbsolutePath();

            project.afterEvaluate(p ->
                    {
                        p.getTasks().withType(Test.class).forEach(task ->
                                {
                                    final List<String> jvmArgs = task.getJvmArgs();
                                    final String newValue = String.format("-javaagent:%s=%s", agentPath, preparedAgentOpts);
                                    jvmArgs.add(newValue);
                                    task.setJvmArgs(jvmArgs);
                                }
                        );
                    }
            );
        } catch (final Exception exception) {
            System.out.println("Failed to find JTeC agent JAR, skipping instrumentation.");
            exception.printStackTrace();
        }
    }

    private String prepareAgentOpts(Project project) {
        final AgentOptions agentOptions;
        JTeCExtension extension = (JTeCExtension) project.getExtensions().findByName(JTEC);
        if (project.hasProperty("jtec.opts")) {
            System.out.println("Parameters: " + project.property("jtec.opts"));
            agentOptions = AgentOptions.fromString((String) project.property("jtec.opts"));
        } else if (extension != null && !extension.getOptions().isEmpty()) {
            agentOptions = AgentOptions.fromString(extension.getOptions());
        } else {
            agentOptions = AgentOptions.DEFAULT_OPTIONS;
        }
        agentOptions.setOutputPath(outputDirectory.toPath());
        String agentString = agentOptions.toAgentString();
        return agentString;
    }

    private void saveReport(Project project) {
        try {
            if (!Files.exists(outputDirectory.toPath())) {
                System.out.println("Report generator could not find JTeC output directory: " + outputDirectory.toString());
                return;
            }
            final ReportGenerator generator = new ReportGenerator(outputDirectory.toPath(), true);
            final TestReport testReport = generator.generateReport(project.getName());
            storeTestReport(testReport);
        } catch (final Exception exception) {
            System.out.println("Failed to generate JTeC report in project " + project.getName());
            exception.printStackTrace();
        }
    }


    final static String TEST_REPORT_FILENAME = "test-report.json";

    boolean storeTestReport(TestReport testReport) throws IOException {
        Path jsonFile = outputDirectory.toPath().resolve(TEST_REPORT_FILENAME);
        Files.deleteIfExists(jsonFile);
        if (testReport.getTestSuites().size() == 0) {
            System.out.println("No Test Suites found during report generation.");
            return false;
        }
        final String jsonTestReport = toJson(testReport);
        createFileAndEnclosingDir(jsonFile);
        writeToFile(jsonFile, jsonTestReport, false, StandardOpenOption.TRUNCATE_EXISTING);
        return true;
    }

}
