package de.junit5project;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.Objects;
import java.util.zip.ZipFile;

public class FileOpener {
    private static void pause(final int ms) {
        try {
            Thread.sleep(ms);
        } catch (final InterruptedException e) {
            System.err.format("IOException: %s%n", e);
        }
    }

    public void openFiles() throws IOException, URISyntaxException {
        final String fileName = "test.txt";
        final Path path = Paths.get(Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).toURI());
        final File file = path.toFile();
        System.out.println("File: " + path);
        int openCounter = 0;
        InputStream is;

        // resource loading
        System.out.format("ClassLoader.getResource (1), opens: %d\n", ++openCounter);
        is = getClass().getClassLoader().getResourceAsStream(fileName);
        Printer.printInputStream(is);
        if (is != null) {
            is.close();
        }

        pause(1000);

        // java.io
        // FileInputStream
        System.out.format("FileInputStream (1), opens: %d\n", ++openCounter);
        final FileInputStream fis = new FileInputStream(file);
        Printer.printInputStream(fis);
        System.out.format("FileInputStream (2), opens: %d\n", ++openCounter);
        final FileInputStream fis2 = new FileInputStream(file);
        Printer.printInputStream(fis2);
        fis2.close();
        fis.close();

        pause(1000);

        // FileOutputStream
        System.out.format("FileOutputStream (1), opens: %d\n", ++openCounter);
        final FileOutputStream fos = new FileOutputStream(file, true);
        fos.write(0);
        fos.close();

        pause(1000);

        // RandomAccessFile
        System.out.format("RandomAccessFile (1), opens: %d\n", ++openCounter);
        final RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.read();
        raf.close();

        pause(1000);

        // ZipFile
        System.out.format("ZipFile (1), opens: %d\n", ++openCounter);
        final ZipFile zip = new ZipFile(Objects.requireNonNull(getClass().getClassLoader().getResource("test.zip")).getFile());
        if (zip.stream().count() > 0) {
            zip.entries().hasMoreElements();
        }
        zip.close();

        pause(1000);

        // java.nio

        // FileSystemProvider
        System.out.format("Files.newInputStream (1), opens: %d\n", ++openCounter);
        // opens 3 times...
        is = Files.newInputStream(path);
        Printer.printInputStream(is);
        is.close();

        pause(1000);

        // Copy files
        System.out.format("Files.copy (1), opens: %d\n", ++openCounter);
        final Path tempFile = Files.createTempFile("open-temp-file", ".tmp");
        tempFile.toFile().deleteOnExit();
        Files.copy(path, tempFile, StandardCopyOption.REPLACE_EXISTING);

        pause(1000);

        // Reading all bytes
        System.out.format("Files.readAllBytes (1), opens: %d\n", ++openCounter);
        final byte[] bytes = Files.readAllBytes(path);
        Printer.printBytes(bytes);

        pause(1000);

        // FileChannel
        System.out.format("FileChannel.open (1), opens: %d\n", ++openCounter);
        final FileChannel fc = FileChannel.open(path, StandardOpenOption.READ);
        fc.close();
    }
}
