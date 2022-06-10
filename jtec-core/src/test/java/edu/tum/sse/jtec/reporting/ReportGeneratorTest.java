package edu.tum.sse.jtec.reporting;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.*;

class ReportGeneratorTest {

    @Test
    void shouldGenerateReportForkingEnabled() throws IOException, URISyntaxException {
        // given
        final Path outputDirectory = Paths.get(this.getClass().getResource("/jtec-logs").toURI()).toAbsolutePath();
        boolean isForking = true;
        final ReportGenerator generator = new ReportGenerator(outputDirectory, isForking);

        // when
        final TestReport testReport = generator.generateReport("sample-report");

        // then
        assertEquals("sample-report", testReport.getReportId());
        assertEquals(14617, testReport.getTotalDuration());
        assertEquals(2, testReport.getTestSuites().size());
        assertEquals("de.junit5project.ThreadStarterTest", testReport.getTestSuites().get(0).getTestId());
        assertEquals(1654880769446L, testReport.getTestSuites().get(0).getStartTimestamp());
        assertEquals(1654880772363L, testReport.getTestSuites().get(0).getEndTimestamp());
        assertEquals(1, testReport.getTestSuites().get(0).getPassed());
        assertEquals(0, testReport.getTestSuites().get(0).getFailed());
        assertEquals(0, testReport.getTestSuites().get(0).getIgnored());
        assertEquals(3, testReport.getTestSuites().get(0).getOpenedFiles().size());
        assertEquals(1, testReport.getTestSuites().get(0).getSpawnedProcesses().size());
        assertEquals(1, testReport.getTestSuites().get(0).getStartedThreads().size());
        assertEquals(6, testReport.getTestSuites().get(0).getCoveredEntities().size());
        assertEquals(1654880781242L, testReport.getTestSuites().get(1).getStartTimestamp());
        assertEquals(1, testReport.getTestSuites().get(1).getOpenedFiles().size());
    }

    @Test
    void shouldGenerateReportForkingDisabled() throws IOException, URISyntaxException {
        // given
        final Path outputDirectory = Paths.get(this.getClass().getResource("/jtec-logs").toURI()).toAbsolutePath();
        boolean isForking = false;
        final ReportGenerator generator = new ReportGenerator(outputDirectory, isForking);

        // when
        final TestReport testReport = generator.generateReport("sample-report");

        // then
        assertEquals(1654880772293L, testReport.getTestSuites().get(0).getStartTimestamp());
        assertEquals(2, testReport.getTestSuites().get(0).getOpenedFiles().size());
        assertEquals(1654880784002L, testReport.getTestSuites().get(1).getStartTimestamp());
        assertEquals(0, testReport.getTestSuites().get(1).getOpenedFiles().size());
    }
}
