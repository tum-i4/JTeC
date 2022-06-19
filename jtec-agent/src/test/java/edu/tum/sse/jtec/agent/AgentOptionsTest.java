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
        String options = "jtec.out=" + tmpDir + "," +
                "test.trace=true," +
                "sys.trace=true," +
                "sys.includes=\".*\"," +
                "sys.excludes=\".*.class\"," +
                "cov.trace=true," +
                "cov.instr," +
                "cov.includes=\".*foo.*\"," +
                "cov.excludes=\".*bar.*\"," +
                "cov.level=method," +
                "init.cmd=run.bat";

        // when
        AgentOptions parsedOptions = AgentOptions.fromString(options);

        // then
        assertTrue(parsedOptions.getOutputPath().toFile().isDirectory());
        assertTrue(parsedOptions.getOutputPath().toFile().exists());
        assertTrue(parsedOptions.shouldTraceSystemEvents());
        assertEquals(parsedOptions.getFileIncludes(), ".*");
        assertEquals(parsedOptions.getFileExcludes(), ".*.class");
        assertTrue(parsedOptions.shouldTraceTestEvents());
        assertTrue(parsedOptions.shouldTraceCoverage());
        assertTrue(parsedOptions.shouldInstrumentCoverage());
        assertEquals(parsedOptions.getCoverageLevel(), CoverageLevel.METHOD);
        assertEquals(parsedOptions.getCoverageIncludes(), ".*foo.*");
        assertEquals(parsedOptions.getCoverageExcludes(), ".*bar.*");
        assertEquals(parsedOptions.getPreTestCommand(), "run.bat");
    }
}
