package edu.tum.sse.jtec.instrumentation;


import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.jar.JarFile;

import static edu.tum.sse.jtec.util.IOUtils.locateJar;

/**
 * Utilities and constants for instrumentation classes.
 */
public class InstrumentationUtils {
    public static final String BYTEBUDDY_PACKAGE = "net.bytebuddy";
    public static final String JTEC_PACKAGE = "edu.tum.sse.jtec";

    /**
     * Append JAR to the bootstrap class loader to make them available when using net.bytebuddy.asm.Advice for
     * instrumenting java classes.
     */
    public static File appendInstrumentationJarFile(final Instrumentation instrumentation, final String jarPath) {
        try {
            instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(jarPath));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        // Create the directory for storing jar files during bootclass injection.
        final File tempFolder;
        try {
            tempFolder = Files.createTempDirectory("agent-bootstrap").toFile();
        } catch (final Exception e) {
            System.err.println("Cannot create temp folder for bootstrap class instrumentation");
            e.printStackTrace(System.err);
            return null;
        }
        return tempFolder;
    }

    public static void replaceJunitTestListenerServiceLoaderManifest(String value) {
        if (value == null)
            value = "";
        try {
            Path jarFile = locateJar(InstrumentationUtils.class);
            try (FileSystem zipFileSystem = FileSystems.newFileSystem(jarFile, null)) {
                Path junit5serviceLoaderManifest = zipFileSystem.getPath("META-INF/services/org.junit.platform.launcher.TestExecutionListener");
                Files.write(junit5serviceLoaderManifest, value.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
