package edu.tum.sse.jtec.reporting;

import com.github.javaparser.Range;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
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

    /**
     * Regex for parsing entities into their components (package.name.Class#method)
     */
    final static Pattern entityRegex = Pattern.compile("^(\\S*)\\.([^#]*)(?:#(\\S*))?$");

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
        List<LCOVSection> lcovSections = new ArrayList<>();

        for (TestSuite testSuite : testReport.getTestSuites()) {
            final Set<String> coveredEntities = testSuite.getCoveredEntities();
            for (String entity : coveredEntities) {
                final String[] entityComponents = getEntityComponents(entity);
                if (entityComponents == null)
                    continue;
                final String packageName = entityComponents[0], className = entityComponents[1], methodName = entityComponents[2];
                final String fullyQualifiedClassName = packageName + "." + className;
                final Path sourceFile = getJavaSourceFile(fullyQualifiedClassName);

                if (sourceFile == null)
                    continue;

                final LCOVSection lcovSection = new LCOVSection(testSuite.getTestId(), sourceFile.toString());
                final String sourceCode = IOUtils.readFromFile(sourceFile);
                final CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);
/*
                Set<Range> methodRanges = compilationUnit
                        .findAll(MethodDeclaration.class).stream()
                        .map(Node::getRange)
                        .filter(Optional::isPresent).map(Optional::get)
                        .collect(Collectors.toSet());

                compilationUnit.getClassByName(className).get().getMethodsByName("");

*/
                final ClassOrInterfaceDeclaration classDeclaration = compilationUnit.getClassByName(className).orElse(null);
                if (classDeclaration == null)
                    continue;
                // TODO: Set default range or ignore entity without range?
                final Range range = classDeclaration.getRange().orElse(null);
                if (range == null)
                    continue;
                for (int line = range.begin.line; line <= range.end.line; line++)
                    lcovSection.addLineHit(line, 1);

                lcovSections.add(lcovSection);
            }
        }

        List<LCOVSection> lcovSectionsFromSourceFiles = initLcovSectionsForSourceFiles();
        lcovSections.addAll(lcovSectionsFromSourceFiles);
        return new LCOVReport(lcovSections);
    }

    /**
     * For each class in each source file, create an LCOV section with hit count 0 for all lines.
     * @return The list of initializd LCOV sections
     */
    private List<LCOVSection> initLcovSectionsForSourceFiles() throws IOException {
        List<LCOVSection> lcovSections = new ArrayList<>();
        for (File file : sourceFiles) {
            final LCOVSection lcovSection = new LCOVSection("", file.getAbsolutePath());
            final String sourceCode = IOUtils.readFromFile(file.toPath());
            final CompilationUnit compilationUnit = StaticJavaParser.parse(sourceCode);
            List<ClassOrInterfaceDeclaration> classDeclarations = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
            List<MethodDeclaration> methodDeclarations = compilationUnit.findAll(MethodDeclaration.class);
            for (MethodDeclaration methodDeclaration : methodDeclarations) {
                final Range range = methodDeclaration.getRange().orElse(null);
                if (range == null)
                    continue;
                for (int line = range.begin.line; line <= range.end.line; line++)
                    lcovSection.addLineHit(line, 0);
                lcovSections.add(lcovSection);
            }
            /*
            for (ClassOrInterfaceDeclaration classDeclaration : classDeclarations) {
                final Range range = classDeclaration.getRange().orElse(null);
                if (range == null)
                    continue;
                for (int line = range.begin.line; line <= range.end.line; line++)
                    lcovSection.addLineHit(line, 0);
                lcovSections.add(lcovSection);
            }*/
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
