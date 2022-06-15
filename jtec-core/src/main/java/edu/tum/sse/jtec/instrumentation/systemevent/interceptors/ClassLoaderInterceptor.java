package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.AdviceOutput;
import edu.tum.sse.jtec.instrumentation.systemevent.MessageWriter;
import net.bytebuddy.asm.Advice;

public class ClassLoaderInterceptor {
    /**
     * Writes the given path to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final String printedName, @AdviceOutput final String outputPath) {
        MessageWriter.writeMessage("OPEN", "RESOURCE", printedName, outputPath);
    }
}
