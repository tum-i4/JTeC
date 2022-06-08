package edu.tum.sse.jtec.instrumentation.coverage;

import edu.tum.sse.jtec.instrumentation.InstrumentationUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.mockito.Mockito.mockStatic;

class CoverageMonitorTest {

    private Path output;

    @BeforeEach
    void setUp() throws IOException {
        output = Files.createTempFile("cov", ".log");
    }

    @AfterEach
    void tearDown() {
        output.toFile().delete();
    }

    @Test
    void shouldDumpCoverage() throws IOException {
        // given
        CoverageMonitor coverageMonitor = CoverageMonitor.create(new ProcessCoverageProbeFactory());
        try (MockedStatic<InstrumentationUtils> utilities = mockStatic(InstrumentationUtils.class)) {
            utilities.when(InstrumentationUtils::getCurrentPid).thenReturn("123");
            coverageMonitor.registerClass("Foo");
            coverageMonitor.registerClass("Bar");
            utilities.when(InstrumentationUtils::getCurrentPid).thenReturn("234");
            coverageMonitor.registerClass("Foo");
        }

        // when
        coverageMonitor.dumpCoverage(output.toAbsolutePath().toString());

        // then
        assertLinesMatch(Arrays.asList("{\"123\":[\"Bar\",\"Foo\"],\"234\":[\"Foo\"]}"), Files.readAllLines(output));
    }
}
