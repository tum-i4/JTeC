package de.junit4project;


import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class FileOpenerTest {
    @Test
    public void testFileInterception() throws IOException, URISyntaxException, InterruptedException {
        new FileOpener().openFiles();
    }
}
