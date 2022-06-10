package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.AdviceOutput;
import edu.tum.sse.jtec.instrumentation.systemevent.AdvicePid;
import net.bytebuddy.asm.Advice;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ProcessStartInterceptor {
    /**
     * Writes the given path to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodExit
    public static void enter(@Advice.Return final Process process, @AdviceOutput final String outputPath, @AdvicePid final String currentPid) {
        final Long timestamp = System.currentTimeMillis();
        try {
            final String pid = process.toString().split(", ")[0].replace("Process[pid=", "");
            final String message = String.format("{\"timestamp\": %d, \"pid\": \"%s\", \"action\": \"SPAWN\", \"target\": \"PROCESS\", \"value\": \"%s\"}\n",
                    timestamp, currentPid, pid);
            Files.write(Paths.get(outputPath), message.getBytes(), StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE);
        } catch (final Exception e) {
            final String pid = process.toString().split(", ")[0].replace("Process[pid=", "");
            System.err.println("Exception, printedName is: " + pid);
            e.printStackTrace();
        }
    }
}
