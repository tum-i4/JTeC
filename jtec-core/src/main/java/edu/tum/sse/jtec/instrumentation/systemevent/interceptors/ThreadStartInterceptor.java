package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.AdviceOutput;
import edu.tum.sse.jtec.instrumentation.systemevent.SysEventWriter;
import net.bytebuddy.asm.Advice;

public class ThreadStartInterceptor {
    /**
     * Writes the given path to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodEnter
    public static void enter(@Advice.FieldValue(value = "name") final String threadName, @AdviceOutput final String outputPath) {
        SysEventWriter.writeMessage("START", "THREAD", threadName, outputPath);
    }
}
