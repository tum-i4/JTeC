package edu.tum.sse.jtec.reporting;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

class LCOVReportParserTest {

    @Test
    void shouldParseTestReportIntoLcovReport() throws URISyntaxException, IOException {
        // given
        final Path baseDirectory = Paths.get(this.getClass().getResource("/sample-classes").toURI()).toAbsolutePath();

        TestSuite testSuite = new TestSuite();
        Set<String> coveredEntities = new HashSet<>(Arrays.asList(
                "com.example.SomeClass",
                "com.example.AnotherClass#<init>()void",
                "com.example.AnotherClass#printBaz()void"));
        testSuite.setCoveredEntities(coveredEntities);
        TestReport testReport = new TestReport("report", 1L, 1L, Collections.singletonList(testSuite));
        LCOVReportParser parser = new LCOVReportParser(baseDirectory);

        // when
        LCOVReport lcovReport = parser.parse(testReport);
        List<LCOVSection> lcovSections = lcovReport.getLcovSections();

        // then
        assertEquals(2, lcovSections.size());
        Optional<LCOVSection> lcovSection1 = lcovSections.stream().filter(s -> s.getSourceFilePath().contains("SomeClass")).findFirst();
        Optional<LCOVSection> lcovSection2 = lcovSections.stream().filter(s -> s.getSourceFilePath().contains("AnotherClass")).findFirst();
        assertTrue(lcovSection1.isPresent());
        assertTrue(lcovSection2.isPresent());
        assertEquals(6, lcovSection1.get().numLinesFound());
        assertEquals(6, lcovSection1.get().numLinesHit());
        assertEquals(2, lcovSection1.get().numMethodsFound());
        assertEquals(2, lcovSection1.get().numMethodsHit());
        assertEquals(9, lcovSection2.get().numLinesFound());
        assertEquals(6, lcovSection2.get().numLinesHit());
        assertEquals(3, lcovSection2.get().numMethodsFound());
        assertEquals(2, lcovSection2.get().numMethodsHit());
    }
}