package edu.tum.sse.jtec.instrumentation;


import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.jar.JarFile;


/**
 * Utilities and constants for instrumentation classes.
 */
public class InstrumentationUtils {
    public static final String BYTEBUDDY_PACKAGE = "net.bytebuddy";
    public static final String JTEC_PACKAGE = "edu.tum.sse.jtec";

    private static final String JTEC_INSTRUMENTATION = "jtec-instrumentation";
    private static final String JAR_FILE_ENDING = ".jar";

    /**
     * Append JAR to the bootstrap class loader to make them available when using net.bytebuddy.asm.Advice for
     * instrumenting java classes.
     */
    public static File appendInstrumentationJarFile(final Instrumentation instrumentation) {
        // Create the directory for storing jar files during bootclass injection.
        final File tempFolder;
        try {
            tempFolder = Files.createTempDirectory("agent-bootstrap").toFile();
            tempFolder.deleteOnExit();
        } catch (final Exception e) {
            System.err.println("Cannot create temp folder for bootstrap class instrumentation");
            e.printStackTrace(System.err);
            return null;
        }
        try {
            final Path tempJarFile = Files.createTempFile(JTEC_INSTRUMENTATION, JAR_FILE_ENDING);
            // We use the "jtec-instrumentation.jar" from the src/main/resources.
            Files.copy(ClassLoader.getSystemResourceAsStream(JTEC_INSTRUMENTATION + JAR_FILE_ENDING), tempJarFile, StandardCopyOption.REPLACE_EXISTING);
            instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(tempJarFile.toFile()));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return tempFolder;
    }
}
