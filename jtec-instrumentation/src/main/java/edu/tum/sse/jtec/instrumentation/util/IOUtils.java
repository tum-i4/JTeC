package edu.tum.sse.jtec.instrumentation.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class IOUtils {

    public static void createFileAndEnclosingDir(final Path path) {
        if (Files.notExists(path)) {
            try {
                if (Files.notExists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }
                Files.createFile(path);
            } catch (final IOException exception) {
                System.err.println("Failed to create file.");
                exception.printStackTrace();
            }
        }
    }

    public static void appendToFile(final Path file, final String message, final boolean lock) throws IOException {
        writeToFile(file, message, lock, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    public static void writeToFile(final Path file, final String message, final boolean lock, final StandardOpenOption... options) throws IOException {
        final byte[] messageAsByteArray = message.getBytes(StandardCharsets.UTF_8);
        if (lock) {
            FileLock fileLock = null;
            try (final FileChannel fileChannel = FileChannel.open(file, options)) {
                fileLock = fileChannel.lock();
                fileChannel.write(ByteBuffer.wrap(messageAsByteArray));
            } catch (final Exception exception) {
                exception.printStackTrace();
                throw new IOException("Problem with locking when writing to file " + file);
            } finally {
                if (fileLock != null && fileLock.isValid()) {
                    fileLock.release();
                }
            }
        } else {
            Files.write(file, messageAsByteArray, options);
        }
    }

}
