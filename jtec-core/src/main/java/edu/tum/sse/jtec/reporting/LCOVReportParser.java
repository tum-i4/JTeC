package edu.tum.sse.jtec.reporting;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import edu.tum.sse.jtec.util.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
                // Get package, class and method from entity; replace constructor name
                final String[] entityComponents = getEntityComponents(entity);
                if (entityComponents == null)
                    continue;
                final String packageName = entityComponents[0];
                final String className = entityComponents[1];
                String tempMethodName = entityComponents[2];
                if (tempMethodName != null && tempMethodName.equals("<init>"))
                    tempMethodName = className;
                final String methodName = tempMethodName;
                final String fullyQualifiedClassName = packageName + "." + className;

                // Find source file containing the class
                // TODO: Build className to sourceFile mapping, query here
                final Path sourceFile = getJavaSourceFile(fullyQualifiedClassName);
                if (sourceFile == null)
                    continue;
                final String sourceFilePath = sourceFile.toString();

                // Get LCOV section describing the source file
                LCOVSection lcovSection = lcovReport.getLcovSection(sourceFilePath);
                if (lcovSection == null)
                    continue;

                // Get class declaration from source file
                final String sourceCode = IOUtils.readFromFile(sourceFile);
                final CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);
                final ClassOrInterfaceDeclaration classDeclaration = compilationUnit.getClassByName(className).orElse(null);
                if (classDeclaration == null)
                    continue;

                // Get either all callables (methods, constructors), or the method `methodName`.
                List<CallableDeclaration> callableDeclarations = classDeclaration
                        .findAll(CallableDeclaration.class).stream()
                        .filter((methodName != null)
                                ? callableDeclaration -> callableDeclaration.getNameAsString().equals(methodName)
                                : callableDeclaration -> true)
                        .collect(Collectors.toList());

                // Add method and line coverage information for callables
                for (CallableDeclaration callableDeclaration : callableDeclarations) {
                    final Range range = callableDeclaration.getRange().orElse(null);
                    if (range == null)
                        continue;
                    lcovSection.addMethodHit(1, callableDeclaration.getNameAsString());
                    for (int line = range.begin.line; line <= range.end.line; line++)
                        lcovSection.addLineHit(line, 1);
                }
            }
        }
        return lcovReport;
    }

    /**
     * For each method in each source file, create an LCOV section with hit count 0 for all lines.
     * @return The list of initialized LCOV sections
     */
    private List<LCOVSection> getInitializedLcovSections() throws IOException {
        List<LCOVSection> lcovSections = new ArrayList<>();
        for (File file : sourceFiles) {
            final LCOVSection lcovSection = new LCOVSection("", file.getAbsolutePath());
            final String sourceCode = IOUtils.readFromFile(file.toPath());
            final CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);

            compilationUnit.findAll(CallableDeclaration.class)
                    .forEach(callableDeclaration -> {
                        final Range range = callableDeclaration.getRange().orElse(null);
                        if (range == null)
                            return;
                        lcovSection.addMethod(range.begin.line, callableDeclaration.getNameAsString());
                        for (int line = range.begin.line; line <= range.end.line; line++)
                            lcovSection.addLineHit(line, 0);
                    }
            );
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
     * If no method name is present, use null for the method.
     * @return An array [package, class, method], or null if no match is found
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
