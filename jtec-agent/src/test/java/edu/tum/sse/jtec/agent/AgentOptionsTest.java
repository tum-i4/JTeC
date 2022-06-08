package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instrumentation.coverage.CoverageLevel;
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
        String options = "test.trace=true," +
                "test.out=" + tmpDir.resolve("test.log") + "," +
                "sys.trace=true," +
                "sys.out=" + tmpDir.resolve("sys.log") + "," +
                "cov.trace=true," +
                "cov.out=" + tmpDir.resolve("cov.log") + "," +
                "cov.instr," +
                "cov.includes=.*foo.*," +
                "cov.excludes=.*bar.*," +
                "cov.level=method";

        // when
        AgentOptions parsedOptions = AgentOptions.fromString(options);

        // then
        assertTrue(parsedOptions.shouldTraceSystemEvents());
        assertEquals(tmpDir.resolve("sys.log"), parsedOptions.getSystemEventOutputPath());
        assertTrue(parsedOptions.getSystemEventOutputPath().toFile().exists());
        assertTrue(parsedOptions.shouldTraceTestEvents());
        assertEquals(tmpDir.resolve("test.log"), parsedOptions.getTestEventOutputPath());
        assertTrue(parsedOptions.getTestEventOutputPath().toFile().exists());
        assertTrue(parsedOptions.shouldTraceCoverage());
        assertEquals(tmpDir.resolve("cov.log"), parsedOptions.getCoverageOutputPath());
        assertTrue(parsedOptions.getCoverageOutputPath().toFile().exists());
        assertTrue(parsedOptions.shouldInstrumentCoverage());
        assertEquals(parsedOptions.getCoverageLevel(), CoverageLevel.METHOD);
        assertEquals(parsedOptions.getCoverageIncludes(), ".*foo.*");
        assertEquals(parsedOptions.getCoverageExcludes(), ".*bar.*");
    }
}
