package de.junit5project;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class FileOpenerTest {
    @Test
    public void testFileInterception() throws IOException, URISyntaxException, InterruptedException {
        new FileOpener().openFiles();
    }
}
