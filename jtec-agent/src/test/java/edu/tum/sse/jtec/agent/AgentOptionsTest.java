package edu.tum.sse.jtec.agent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AgentOptionsTest {

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
    void shouldParseOptionsFromString() {
        // given
        String options = "traceTestEvents=true," +
                "testEventOut=" + tmpDir.resolve("test.log") + "," +
                "traceSysEvents=true," +
                "sysEventOut=" + tmpDir.resolve("sys.log");

        // when
        AgentOptions parsedOptions = AgentOptions.fromString(options);

        // then
        assertTrue(parsedOptions.shouldTraceSystemEvents());
        assertEquals(tmpDir.resolve("sys.log"), parsedOptions.getSystemEventOutputPath());
        assertTrue(parsedOptions.getSystemEventOutputPath().toFile().exists());
        assertTrue(parsedOptions.shouldTraceTestEvents());
        assertEquals(tmpDir.resolve("test.log"), parsedOptions.getTestEventOutputPath());
        assertTrue(parsedOptions.getTestEventOutputPath().toFile().exists());
    }
}
