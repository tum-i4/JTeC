package edu.tum.sse.jtec.util;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import edu.tum.sse.jtec.reporting.LCOVReport;
import edu.tum.sse.jtec.reporting.LCOVReportParser;
import edu.tum.sse.jtec.reporting.TestReport;
import edu.tum.sse.jtec.reporting.TestSuite;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class LCOVUtils {
    /*
    public static String toLcov(TestReport testReport, File baseDir) throws IOException {
        // TODO: Make non-static, refactor into methods
        StringBuilder lcovTestReport = new StringBuilder();
        final String[] fileExtensions = {"java"};
        final Collection<File> javaSourceFiles = FileUtils.listFiles(baseDir, fileExtensions, true);

        for (TestSuite testSuite : testReport.getTestSuites()) {
            final Set<String> coveredEntities = testSuite.getCoveredEntities();
            for (String entity : coveredEntities) {
                final Path sourceFile = getSourceFile(javaSourceFiles, entity);
                if (sourceFile == null)
                    continue;

                // TODO: get covered lines (use entire class/method)
                final String sourceCode = IOUtils.readFromFile(sourceFile);
                final String entityName = entity.substring(entity.lastIndexOf('.') + 1);
                final CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

                final ClassOrInterfaceDeclaration classDeclaration = compilationUnit.getClassByName(entityName).orElse(null);
                if (classDeclaration == null)
                    continue;
                // TODO: Set default range or ignore entity without range?
                final Range range = classDeclaration.getRange().orElse(Range.range(0,0,0,0));

                final int numLinesFound = range.getLineCount();
                final int numLinesHit = range.getLineCount();

                // TODO: add class LCOVSection for single source file
                lcovTestReport.append("TN:").append(testSuite.getTestId()).append('\n');
                lcovTestReport.append("SF:").append(sourceFile.toAbsolutePath()).append('\n');
                for (int line = range.begin.line; line <= range.end.line; line++)
                    lcovTestReport.append("DA:").append(line).append(",1\n");
                lcovTestReport.append("LF:").append(numLinesFound).append('\n');
                lcovTestReport.append("LH:").append(numLinesHit).append('\n');
                lcovTestReport.append("end_of_record\n");
            }
        }
        return lcovTestReport.toString();
    }
    */
    public static String toLcov(TestReport testReport, File baseDir) throws IOException {
        LCOVReportParser lcovReportParser = new LCOVReportParser(baseDir);
        LCOVReport lcovReport = lcovReportParser.parse(testReport);
        return lcovReport.toString();
    }

    private static Path getSourceFile(Collection<File> javaSourceFiles, String className) {
        final String relativeFilePath = className.replace('.','/') + ".java";
        final Optional<Path> sourceFile = javaSourceFiles.stream().map(File::toPath).filter(p -> p.endsWith(relativeFilePath)).findFirst();
        return sourceFile.orElse(null);
    }
}
