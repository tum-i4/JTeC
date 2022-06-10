package edu.tum.sse.jtec.reporting;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

class ReportGeneratorTest {

    @Test
    void shouldGenerateReport() throws IOException {
        // given
        final Path outputDirectory = Paths.get(Objects.requireNonNull(this.getClass().getResource("/jtec-logs")).getPath());
        final ReportGenerator generator = new ReportGenerator(outputDirectory, true);

        // when
        final TestReport testReport = generator.generateReport("sample-report");


        // then

    }
}
