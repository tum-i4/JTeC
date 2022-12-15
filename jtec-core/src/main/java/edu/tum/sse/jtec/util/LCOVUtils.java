package edu.tum.sse.jtec.util;

import edu.tum.sse.jtec.reporting.LCOVReport;
import edu.tum.sse.jtec.reporting.LCOVReportParser;
import edu.tum.sse.jtec.reporting.TestReport;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class LCOVUtils {
    /**
     * Convert a test report into a tracefile in the LCOV format.
     * @param testReport The test report to parse
     * @param baseDirectory The root directory of the test report's project
     * @return The LCOV tracefile as a String
     * @throws IOException
     */
    public static String toLcov(TestReport testReport, Path baseDirectory) throws IOException {
        LCOVReportParser lcovReportParser = new LCOVReportParser(baseDirectory);
        LCOVReport lcovReport = lcovReportParser.parse(testReport);
        return lcovReport.toString();
    }
}
