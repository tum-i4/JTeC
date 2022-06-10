package edu.tum.sse.jtec.reporting;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ReportGeneratorTest {

    @Test
    void shouldGenerateReport() throws IOException {
        // given
        Path outputDirectory = Paths.get(Objects.requireNonNull(this.getClass().getResource("jtec-logs")).getPath());
        ReportGenerator generator = new ReportGenerator(outputDirectory);

        // when
        TestReport testReport = generator.generateReport("sample-report");

        // then

    }
}
