package edu.tum.sse.jtec.instrumentation.coverage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

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
    void shouldSaveCoverageMapToFile() throws IOException {
        // given
        final CoverageMonitor coverageMonitor = CoverageMonitor.create();
        coverageMonitor.registerClass("Foo");
        coverageMonitor.registerClass("Bar");
        coverageMonitor.registerDump("123");
        coverageMonitor.registerClass("Foo");
        coverageMonitor.registerDump("234");

        // when
        coverageMonitor.saveCoverage(output.toAbsolutePath().toString());

        // then
        assertLinesMatch(Collections.singletonList("{\"123\":[\"Bar\",\"Foo\"],\"234\":[\"Foo\"]}"), Files.readAllLines(output));
    }

    @Test
    void shouldDumpCoverageWithTestSuiteNames() throws IOException {
        // given
        final CoverageMonitor coverageMonitor = CoverageMonitor.create();
        coverageMonitor.registerClass("Foo");
        coverageMonitor.registerDump("FooTest");
        coverageMonitor.registerClass("Foo");
        coverageMonitor.registerClass("Bar");
        coverageMonitor.registerDump("BarTest");
        coverageMonitor.registerClass("Baz");
        Map<String, Set<String>> expectedMap = new HashMap<>();
        expectedMap.put("FooTest", Sets.newSet("Foo"));
        expectedMap.put("BarTest", Sets.newSet("Foo", "Bar"));

        // when
        CoverageMap actualMap = coverageMonitor.getCoverageMap();

        // then
        assertEquals(expectedMap, actualMap.getCollectedProbes());
    }
}
