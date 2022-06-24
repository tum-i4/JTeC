package edu.tum.sse.jtec.reporting;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TestingLogParserTest {

    @Test
    void shouldParseMultipleTestSuiteFinishedEvents() throws URISyntaxException, IOException {
        // given
        final Path testLog = Paths.get(this.getClass().getResource("/97454_1654880769446_test.log").toURI()).toAbsolutePath();
        boolean isForking = true;
        TestingLogParser parser = new TestingLogParser(isForking);

        // when
        final Map<String, List<TestSuite>> testSuiteMap = parser.parse(testLog);

        // then
        assertEquals(1, testSuiteMap.size());
        assertTrue(testSuiteMap.containsKey("97454"));
        assertEquals("de.junit5project.ThreadStarterTest", testSuiteMap.get("97454").get(0).getTestId());
        assertEquals(100, testSuiteMap.get("97454").get(0).getDuration());
        assertEquals(1654880769446L, testSuiteMap.get("97454").get(0).getStartTimestamp());
        assertEquals(1654880772393L, testSuiteMap.get("97454").get(0).getEndTimestamp());
        assertEquals(2, testSuiteMap.get("97454").get(0).getRunCount());
    }
}
