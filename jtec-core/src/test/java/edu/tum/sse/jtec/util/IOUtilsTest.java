package edu.tum.sse.jtec.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static edu.tum.sse.jtec.util.IOUtils.appendToFile;
import static edu.tum.sse.jtec.util.IOUtils.createFileAndEnclosingDir;
import static org.junit.jupiter.api.Assertions.*;

class IOUtilsTest {

    Path tmpDir;

    @BeforeEach
    void setUp() throws IOException {
        tmpDir = Files.createTempDirectory("tmpDirPrefix");
    }

    @AfterEach
    void tearDown() {
        tmpDir.toFile().delete();
    }

    @Test
    void shouldCreateFileAndEnclosingDir() throws IOException {
        // given
        Path file = tmpDir.resolve("foo").resolve("bar.txt");
        assertFalse(Files.exists(file.getParent()));
        assertFalse(Files.exists(file));

        // when
        createFileAndEnclosingDir(file);

        // then
        assertTrue(Files.exists(file.getParent()));
        assertTrue(Files.exists(file));
    }

    @Test
    void shouldAppendToFile() throws IOException {
        // given
        Path file = tmpDir.resolve("foo.txt");

        // when
        appendToFile(file, "bar", true);

        // then
        assertTrue(Files.exists(file));
        assertEquals(Arrays.asList("bar"), Files.readAllLines(file));
    }

    @Test
    void shouldAppendToExistingFile() throws IOException {
        // given
        Path file = tmpDir.resolve("foo.txt");
        Files.write(file, "foo\n".getBytes());

        // when
        appendToFile(file, "bar", true);

        // then
        assertTrue(Files.exists(file));
        assertEquals(Arrays.asList("foo", "bar"), Files.readAllLines(file));
    }
}
