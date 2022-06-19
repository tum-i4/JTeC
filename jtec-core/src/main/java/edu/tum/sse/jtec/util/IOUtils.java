package edu.tum.sse.jtec.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;

public final class IOUtils {

    public static void createFileAndEnclosingDir(Path path) {
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

    public static void removeDir(Path path) throws IOException {
        if (path.toFile().exists()) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    public static void appendToFile(Path file, String message, boolean lock) throws IOException {
        writeToFile(file, message, lock, StandardOpenOption.APPEND, StandardOpenOption.CREATE);
    }

    public static void writeToFile(Path file, String message, boolean lock, StandardOpenOption... options) throws IOException {
        byte[] messageAsByteArray = message.getBytes(StandardCharsets.UTF_8);
        if (lock) {
            FileLock fileLock = null;
            try (FileChannel fileChannel = FileChannel.open(file, options)) {
                fileLock = fileChannel.lock();
                fileChannel.write(ByteBuffer.wrap(messageAsByteArray));
            } catch (Exception exception) {
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
