package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.AdviceOutput;
import edu.tum.sse.jtec.instrumentation.systemevent.SysEventWriter;
import edu.tum.sse.jtec.instrumentation.systemevent.SystemInstrumentationEvent;
import net.bytebuddy.asm.Advice;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Interceptor that handles method calls with {@link String} parameter.
 */
public class StringPathInterceptor {

    /**
     * Writes the given path to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final String printedName, @AdviceOutput final String outputPath) {
        final Path outputFile = Paths.get(outputPath);
        if (outputFile.getFileName().equals(Paths.get(printedName).getFileName())) {
            return;
        }
        SysEventWriter.writeMessage(SystemInstrumentationEvent.Action.OPEN, SystemInstrumentationEvent.Target.FILE, printedName, outputPath);
    }
}
