package edu.tum.sse.jtec.reporting;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

/**
 * Parse a {@link TestReport} into an {@link LCOVReport}.
 */
public class LCOVReportParser {
    /**
     * The base directory of the test report's project
     */
    private File baseDir;

    /**
     * Extensions of project files to consider in LCOV report
     */
    private final String[] FILE_EXTENSIONS = {"java"};

    /**
     * Project files to add to LCOV report
     */
    private Collection<File> sourceFiles;


    public LCOVReportParser(File baseDir) {
        setBaseDir(baseDir);
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
        this.sourceFiles = FileUtils.listFiles(baseDir, FILE_EXTENSIONS, true);
    }

    public LCOVReport parse(TestReport testReport) {
        List<LCOVSection> lcovSections = new ArrayList<>();

        for (TestSuite testSuite : testReport.getTestSuites()) {
            final Set<String> coveredEntities = testSuite.getCoveredEntities();
            for (String entity : coveredEntities) {

            }
        }

        return new LCOVReport(lcovSections);
    }

    /**
     * Get the Java source file corresponding to a class
     * @param className The fully qualified class name
     * @return The source file if it exists, else null
     */
    private Path getJavaSourceFile(String className) {
        final String relativeFilePath = className.replace('.','/') + ".java";
        final Optional<Path> sourceFile = sourceFiles.stream().map(File::toPath).filter(p -> p.endsWith(relativeFilePath)).findFirst();
        return sourceFile.orElse(null);
    }
}
