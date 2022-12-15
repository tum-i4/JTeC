package edu.tum.sse.jtec.reporting;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import edu.tum.sse.jtec.util.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parse a {@link TestReport} into an {@link LCOVReport}.
 */
public class LCOVReportParser {
    /**
     * The root directory of the test report's project
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

    /**
     * Regex for parsing entities into their components (package.name.Class#method()returnType)
     */
    final static Pattern entityRegex = Pattern.compile("^([^#(]*)\\.([^#(]*)(?:#(\\S*)\\(.+)?$");

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

    public LCOVReport parse(TestReport testReport) throws IOException {
        List<LCOVSection> lcovSections = getInitializedLcovSections();
        LCOVReport lcovReport = new LCOVReport(lcovSections);

        for (TestSuite testSuite : testReport.getTestSuites()) {
            for (String entity : testSuite.getCoveredEntities()) {
                final String[] entityComponents = getEntityComponents(entity);
                if (entityComponents == null)
                    continue;
                final String packageName = entityComponents[0], className = entityComponents[1], methodName = entityComponents[2];
                final String fullyQualifiedClassName = packageName + "." + className;

                final Path sourceFile = getJavaSourceFile(fullyQualifiedClassName);
                if (sourceFile == null)
                    continue;
                final String sourceFilePath = sourceFile.toString();

                final String sourceCode = IOUtils.readFromFile(sourceFile);
                final CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);
                final ClassOrInterfaceDeclaration classDeclaration = compilationUnit.getClassByName(className).orElse(null);
                if (classDeclaration == null)
                    continue;

                LCOVSection lcovSection = lcovReport.getLcovSection(sourceFilePath);
                if (lcovSection == null)
                    continue;
                final List<MethodDeclaration> methodDeclarations = (methodName != null) ? classDeclaration.getMethodsByName(methodName) : classDeclaration.getMethods();
                for (MethodDeclaration methodDeclaration : methodDeclarations) {
                    final Range range = methodDeclaration.getRange().orElse(null);
                    if (range == null)
                        continue;
                    lcovSection.addMethodHit(1, methodDeclaration.getNameAsString());
                    for (int line = range.begin.line; line <= range.end.line; line++)
                        lcovSection.addLineHit(line, 1);
                }
            }
        }
        return lcovReport;
    }

    /**
     * For each method in each source file, create an LCOV section with hit count 0 for all lines.
     * @return The list of initializd LCOV sections
     */
    private List<LCOVSection> getInitializedLcovSections() throws IOException {
        List<LCOVSection> lcovSections = new ArrayList<>();
        for (File file : sourceFiles) {
            final LCOVSection lcovSection = new LCOVSection("", file.getAbsolutePath());
            final String sourceCode = IOUtils.readFromFile(file.toPath());
            final CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);
            List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
            for (MethodDeclaration methodDeclaration : methodDeclarations) {
                final Range range = methodDeclaration.getRange().orElse(null);
                if (range == null)
                    continue;
                lcovSection.addMethod(range.begin.line, methodDeclaration.getNameAsString());
                for (int line = range.begin.line; line <= range.end.line; line++)
                    lcovSection.addLineHit(line, 0);
            }
            lcovSections.add(lcovSection);
        }
        return lcovSections;
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

    /**
     * Extract the package name, class name, and method name from an entity string.
     * If no method name is present, use null.
     * @return An array [package, class, method]
     */
    public static String[] getEntityComponents(String entity) {
        Matcher matcher = entityRegex.matcher(entity);
        if (matcher.matches()) {
            MatchResult result = matcher.toMatchResult();
            return new String[] {result.group(1), result.group(2), result.group(3)};
        }
        return null;
    }
}
