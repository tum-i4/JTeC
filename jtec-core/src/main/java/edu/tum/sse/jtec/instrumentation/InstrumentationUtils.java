package edu.tum.sse.jtec.instrumentation;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Utilities and constants for instrumentation classes.
 */
public class InstrumentationUtils {
    public static final String BYTEBUDDY_PACKAGE = "net.bytebuddy";
    public static final String JTEC_PACKAGE = "edu.tum.sse.jtec";

    public static String getCurrentPid() {
        return java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }

    public static void writeToFileLocking(String filePath, String message) throws IOException {
        byte[] messageAsByteArray = (message + "\n").getBytes(StandardCharsets.UTF_8);
        FileLock fileLock = null;
        try (FileChannel fileChannel = FileChannel.open(Paths.get(filePath), new StandardOpenOption[]{StandardOpenOption.APPEND})) {
            fileLock = fileChannel.lock();
            fileChannel.write(ByteBuffer.wrap(messageAsByteArray));
        } catch (Exception exception) {
            throw new IOException("Problem arose related to writing to file: " + exception.getMessage());
        } finally {
            if (fileLock != null) {
                fileLock.release();
            }
        }
    }
}
