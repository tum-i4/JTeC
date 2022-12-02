package edu.tum.sse.jtec.instrumentation.coverage;

import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CoverageMonitorTest {

    @Test
    void shouldDumpCoverageWithTestSuiteNames() {
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
