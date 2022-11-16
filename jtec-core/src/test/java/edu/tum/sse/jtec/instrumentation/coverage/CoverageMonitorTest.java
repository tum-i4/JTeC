package edu.tum.sse.jtec.instrumentation.coverage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.mockito.Mockito.*;

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
    void shouldDumpCoverageWithPidStrategy() throws IOException {
        // given
        final CoverageIdStrategy strategy = spy(PIDStrategy.getInstance());
        final CoverageMonitor coverageMonitor = CoverageMonitor.create(strategy);
        when(strategy.getId()).thenReturn("123");
        coverageMonitor.registerClass("Foo");
        coverageMonitor.registerClass("Bar");
        when(strategy.getId()).thenReturn("234");
        coverageMonitor.registerClass("Foo");

        // when
        coverageMonitor.dumpCoverage(output.toAbsolutePath().toString());

        // then
        assertLinesMatch(Collections.singletonList("{\"123\":[\"Bar\",\"Foo\"],\"234\":[\"Foo\"]}"), Files.readAllLines(output));
    }

    @Test
    void shouldCorrectlyMapCoverageWithTestIdStrategy() throws IOException {
        // given
        final TestIdStrategy strategy = TestIdStrategy.getInstance();
        final CoverageMonitor coverageMonitor = CoverageMonitor.create(strategy);
        coverageMonitor.registerClass("Foo");
        strategy.setTestId("FooTest");
        coverageMonitor.registerClass("Foo");
        coverageMonitor.registerClass("Bar");
        strategy.setTestId("BarTest");
        coverageMonitor.registerClass("Baz");
        CoverageMap expectedMap = new CoverageMap();
        expectedMap
                .put(TestIdStrategy.TestPhaseId.GLOBAL_SETUP.toString(), "Foo")
                .put("FooTest", "Foo")
                .put("FooTest", "Bar")
                .put("BarTest", "Baz");

        // when
        CoverageMap actualMap = coverageMonitor.getCoverageMap();

        // then
        assertEquals(expectedMap, actualMap);
    }
}
