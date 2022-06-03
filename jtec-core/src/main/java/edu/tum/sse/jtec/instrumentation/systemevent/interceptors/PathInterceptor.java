package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.AdviceOutput;
import edu.tum.sse.jtec.instrumentation.systemevent.AdvicePid;
import net.bytebuddy.asm.Advice;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Interceptor that handles method calls with {@link Path} parameter.
 */
public class PathInterceptor {

    /**
     * Writes the given path to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final Path path, @AdviceOutput final String outputPath, @AdvicePid final String currentPid) {
        final Long timestamp = System.currentTimeMillis() * 1000000;
        try {
            final Path outputFile = Paths.get(outputPath);
            if (outputFile.getFileName().equals(path.getFileName())) {
                return;
            }
            final String message = String.format("{\"timestamp\": %d, \"pid\": \"%s\", \"action\": \"OPEN\", \"target\": \"FILE\", \"value\": \"%s\"}\n",
                    timestamp, currentPid, path);
            Files.write(outputFile, message.getBytes(), StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE);
        } catch (final Exception e) {
            System.err.println("Exception, path is: " + path);
            e.printStackTrace();
        }
    }
}
