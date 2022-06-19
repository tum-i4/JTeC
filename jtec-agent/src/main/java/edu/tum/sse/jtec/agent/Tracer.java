package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instrumentation.AbstractInstrumentation;
import edu.tum.sse.jtec.instrumentation.InstrumentationUtils;
import edu.tum.sse.jtec.instrumentation.coverage.CoverageInstrumentation;
import edu.tum.sse.jtec.instrumentation.systemevent.SystemEventInstrumentation;
import edu.tum.sse.jtec.instrumentation.testevent.TestEventInstrumentation;
import edu.tum.sse.jtec.util.IOUtils;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static edu.tum.sse.jtec.util.IOUtils.createFileAndEnclosingDir;
import static edu.tum.sse.jtec.util.ProcessUtils.getCurrentPid;

/**
 * A {@link Tracer} registers transformers that perform bytecode instrumentation based on {@link AgentOptions}.
 */
public class Tracer {
    private final Instrumentation instrumentation;
    private final List<AbstractInstrumentation> customInstrumentationList;

    public Tracer(final Instrumentation instrumentation, final AgentOptions options) {
        this.instrumentation = instrumentation;
        customInstrumentationList = new ArrayList<>();

        final File tempFolder = InstrumentationUtils.appendInstrumentationJarFile(instrumentation, getInstrumentationLocation());
        if (tempFolder == null) return;

        // Order matters here: (1) test events, (2) system, (3) coverage
        if (options.shouldTraceTestEvents()) {
            final Path testEventOutput = options.getOutputPath().resolve(String.format("%s_%d_test.log", getCurrentPid(), System.currentTimeMillis()));
            createFileAndEnclosingDir(testEventOutput);
            customInstrumentationList.add(new TestEventInstrumentation(testEventOutput.toString()).attach(instrumentation, tempFolder));
        }
        if (options.shouldTraceSystemEvents()) {
            final Path sysEventOutput = options.getOutputPath().resolve(String.format("%s_%d_sys.log", getCurrentPid(), System.currentTimeMillis()));
            createFileAndEnclosingDir(sysEventOutput);
            customInstrumentationList.add(new SystemEventInstrumentation(sysEventOutput.toString(), options.getFileIncludes(), options.getFileExcludes()).attach(instrumentation, tempFolder));
        }
        if (options.shouldTraceCoverage()) {
            final Path covEventOutput = options.getOutputPath().resolve(String.format("%s_%d_cov.log", getCurrentPid(), System.currentTimeMillis()));
            customInstrumentationList.add(
                    new CoverageInstrumentation(
                            covEventOutput.toString(),
                            options.getCoverageLevel(),
                            options.getCoverageIncludes(),
                            options.getCoverageExcludes(),
                            options.shouldInstrumentCoverage()
                    ).attach(instrumentation, tempFolder));
        }
    }

    /**
     * Returns the directory that contains the agent or null if it can't be resolved.
     */
    public static Path getJTecInstallDirectory() {
        try {
            final URI jarFileUri = IOUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            // we assume that the dist zip is extracted and the agent jar not moved
            return Paths.get(jarFileUri).getParent().getParent().getParent();
        } catch (final URISyntaxException e) {
            throw new RuntimeException("Failed to obtain agent directory. This is a bug, please report it.", e);
        }
    }

    private String getInstrumentationLocation() {
        final Path jTecInstallDirectory = getJTecInstallDirectory();
        final String implementationVersion = getClass().getPackage().getImplementationVersion();
        if (jTecInstallDirectory.toAbsolutePath().toString().contains(".m2")) {
            return String.format("%s/jtec-instrumentation/%s/jtec-instrumentation-%s.jar", jTecInstallDirectory, implementationVersion, implementationVersion);
        } else if (jTecInstallDirectory.toAbsolutePath().toString().contains("target")) {
            return String.format("%s/jtec-instrumentation/target/jtec-instrumentation-%s.jar", jTecInstallDirectory, implementationVersion);
        }
        throw new RuntimeException("Could not find instrumentation Jar File in " + jTecInstallDirectory);
    }
}
