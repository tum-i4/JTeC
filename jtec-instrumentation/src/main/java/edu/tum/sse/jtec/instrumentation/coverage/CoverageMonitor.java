package edu.tum.sse.jtec.instrumentation.coverage;

import edu.tum.sse.jtec.util.IOUtils;
import edu.tum.sse.jtec.util.ProcessUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CoverageMonitor {
    private final CoverageMap coverageMap = new CoverageMap();

    private CoverageMonitor() {
    }

    public static CoverageMonitor create() {
        return new CoverageMonitor();
    }

    public void registerClass(final String className) {
        registerCoverage(className);
    }

    public void registerMethodCall(final String className, final String methodSignature, final String returnType) {
        registerCoverage(className + methodSignature + returnType);
    }

    private void registerCoverage(String value) {
        String pid = ProcessUtils.getCurrentPid();
        coverageMap.put(pid, value);
    }

    public void clearCoverage() {
        coverageMap.clear();
    }

    public void dumpCoverage(final String outputPath) throws IOException {
        final String json = coverageMap.toJson();
        final Path outputFile = Paths.get(outputPath);
        IOUtils.createFileAndEnclosingDir(outputFile);
        IOUtils.appendToFile(outputFile, json, true);
        clearCoverage();
    }
}
