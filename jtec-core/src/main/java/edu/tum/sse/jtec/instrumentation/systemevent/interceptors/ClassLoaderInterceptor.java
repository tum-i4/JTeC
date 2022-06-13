package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.AdviceOutput;
import edu.tum.sse.jtec.instrumentation.systemevent.AdvicePid;
import net.bytebuddy.asm.Advice;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ClassLoaderInterceptor {
    /**
     * Writes the given path to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final String printedName, @AdviceOutput final String outputPath, @AdvicePid final String currentPid) {
        final Long timestamp = System.currentTimeMillis();
        try {
            final String message = String.format("{\"timestamp\": %d, \"pid\": \"%s\", \"action\": \"OPEN\", \"target\": \"RESOURCE\", \"value\": \"%s\"}\n",
                    timestamp, currentPid, printedName);
            Files.write(Paths.get(outputPath), message.getBytes(), StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE);
        } catch (final Exception e) {
            System.err.println("Exception, printedName is: " + printedName);
            e.printStackTrace();
        }
    }
}
