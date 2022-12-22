package edu.tum.sse.jtec.reporting;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LCOVReportTest {

    @Test
    void shouldGetLcovSection() {
        // given
        String sourceFilePath = "path/to/file_1";
        LCOVSection lcovSection = new LCOVSection("test-1", sourceFilePath);
        List<LCOVSection> lcovSections = Arrays.asList(
                new LCOVSection("test-1", "path/to/file_2"),
                lcovSection
        );
        LCOVReport lcovReport = new LCOVReport(lcovSections);

        // when
        Optional<LCOVSection> actualLcovSection = lcovReport.getLcovSection(sourceFilePath);

        // then
        assertTrue(actualLcovSection.isPresent());
        assertEquals(actualLcovSection.get(), lcovSection);
    }
}