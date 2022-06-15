package edu.tum.sse.jtec.instrumentation.systemevent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class SysEventWriter {

    public static void writeMessage(final String action, final String target, final String value, final String outputPath) {
        final Long timestamp = System.currentTimeMillis();
        final String message = String.format("{\"timestamp\": %d, \"pid\": \"%s\", \"action\": \"%s\", \"target\": \"%s\", \"value\": \"%s\"}\n",
                timestamp, getCurrentPid(), action, target, value);
        try {
            Files.write(Paths.get(outputPath), message.getBytes(), StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE);
        } catch (final IOException e) {
            System.err.println("Exception, printedName is: " + value);
            e.printStackTrace();
        }
    }

    private static String getCurrentPid() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

}
