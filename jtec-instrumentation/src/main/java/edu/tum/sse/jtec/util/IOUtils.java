package edu.tum.sse.jtec.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;

public final class IOUtils {

    public static <T> Path locateJar(Class<T> clazz) throws IOException, URISyntaxException {
        URL url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class");
        URI jarURL = ((JarURLConnection) url.openConnection()).getJarFileURL().toURI();
        return Paths.get(jarURL);
    }

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

    public static void removeDir(final Path path) throws IOException {
        if (path.toFile().exists()) {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
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

    public static String readFromFile(final Path file, final Charset encoding) throws IOException
    {
        byte[] encoded = Files.readAllBytes(file);
        return new String(encoded, encoding);
    }

    public static String readFromFile(final Path file) throws IOException {
        return readFromFile(file, StandardCharsets.UTF_8);
    }

}
