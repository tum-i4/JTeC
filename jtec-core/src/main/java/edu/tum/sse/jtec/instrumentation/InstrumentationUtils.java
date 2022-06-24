package edu.tum.sse.jtec.instrumentation;


import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.jar.JarFile;


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
}
