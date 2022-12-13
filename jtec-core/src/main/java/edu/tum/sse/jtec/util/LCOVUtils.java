package edu.tum.sse.jtec.util;

import edu.tum.sse.jtec.reporting.TestReport;
import edu.tum.sse.jtec.reporting.TestSuite;

import java.util.Set;

public class LCOVUtils {
    public static String toLcov(TestReport testReport)  {
        StringBuilder lcovTestReport = new StringBuilder();
        for (TestSuite testSuite : testReport.getTestSuites()) {
            final Set<String> coveredEntities = testSuite.getCoveredEntities();
            // TODO: TN at the start of every entity's section?
            lcovTestReport.append("TN:").append(testSuite.getTestId()).append('\n');

            // TODO: filter for entities in project
            for (String entity : coveredEntities) {
                // TODO: find associated source file
                String sourceFilePath = getSourceFilePath(entity);

                // TODO: get covered lines (use entire class/method)

                // TODO: Append section to lcov report
                lcovTestReport.append("SF:").append(sourceFilePath).append('\n');
                // TODO: Add covered line data (DA, LF, LH)
                lcovTestReport.append("end_of_record\n");
            }
        }
        return lcovTestReport.toString();
    }

    private static String getSourceFilePath(String className) {
        // TODO Retrieve full source file path using JavaParser or compiler output (inputFiles.lst)
        final String relativeFilePath = className.replace('.','/') + ".java";
        return className.replace('.','/') + ".java";
    }
}
