package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.AdviceOutput;
import edu.tum.sse.jtec.instrumentation.systemevent.SysEventWriter;
import net.bytebuddy.asm.Advice;

public class ProcessStartInterceptor {
    /**
     * Writes the given path to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodExit
    public static void enter(@Advice.Return final Process process, @AdviceOutput final String outputPath) {
        final String pid = process.toString().split(", ")[0].replace("Process[pid=", "");
        SysEventWriter.writeMessage("SPAWN", "PROCESS", pid, outputPath);
    }
}
