package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instrumentation.coverage.CoverageLevel;
import edu.tum.sse.jtec.util.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldParseOptionsFromString() throws IOException {
        // given
        Path optionsFile = tmpDir.resolve("jtec.txt");
        IOUtils.writeToFile(optionsFile, "sys.socket=true,test.trace=true", false);
        String options = "jtec.out=" + tmpDir + "," +
                "jtec.optsfile=" + optionsFile + "," +
                "test.trace=true," +
                "test.instr=false," +
                "sys.trace=true," +
                "sys.file=true," +
                "sys.socket=false," +
                "sys.thread=true," +
                "sys.process=false," +
                "sys.includes=\".*\"," +
                "sys.excludes=\".*.class\"," +
                "cov.trace=true," +
                "cov.instr," +
                "cov.includes=\".*foo.*\"," +
                "cov.excludes=\".*bar.*\"," +
                "cov.level=method," +
                "init.cmd=\"run.bat\"";

        // when
        AgentOptions parsedOptions = AgentOptions.fromString(options);

        // then
        assertTrue(parsedOptions.getOutputPath().toFile().isDirectory());
        assertTrue(parsedOptions.getOutputPath().toFile().exists());
        assertTrue(parsedOptions.getOptionsFile().toFile().exists());
        assertTrue(parsedOptions.shouldTraceSystemEvents());
        assertTrue(parsedOptions.shouldInstrumentFileEvents());
        assertTrue(parsedOptions.shouldInstrumentThreadEvents());
        assertTrue(parsedOptions.shouldInstrumentSocketEvents());  // set via options file
        assertFalse(parsedOptions.shouldInstrumentProcessEvents());
        assertEquals(parsedOptions.getFileIncludes(), ".*");
        assertEquals(parsedOptions.getFileExcludes(), ".*.class");
        assertTrue(parsedOptions.shouldTraceTestEvents());
        assertFalse(parsedOptions.shouldInstrumentTestEvents());
        assertTrue(parsedOptions.shouldTraceCoverage());
        assertTrue(parsedOptions.shouldInstrumentCoverage());
        assertEquals(parsedOptions.getCoverageLevel(), CoverageLevel.METHOD);
        assertEquals(parsedOptions.getCoverageIncludes(), ".*foo.*");
        assertEquals(parsedOptions.getCoverageExcludes(), ".*bar.*");
        assertEquals(parsedOptions.getPreTestCommand(), "run.bat");
    }
}
