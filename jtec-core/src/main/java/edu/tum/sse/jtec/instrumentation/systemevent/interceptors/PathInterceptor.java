package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.AdviceOutput;
import edu.tum.sse.jtec.instrumentation.systemevent.MessageWriter;
import net.bytebuddy.asm.Advice;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Interceptor that handles method calls with {@link Path} parameter.
 */
public class PathInterceptor {

    /**
     * Writes the given path to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final Path path, @AdviceOutput final String outputPath) {
        final Path outputFile = Paths.get(outputPath);
        if (outputFile.getFileName().equals(path.getFileName())) {
            return;
        }
        MessageWriter.writeMessage("OPEN", "FILE", path.toString(), outputPath);
    }
}
