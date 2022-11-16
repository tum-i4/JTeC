package edu.tum.sse.jtec.instrumentation.coverage;

import edu.tum.sse.jtec.util.IOUtils;
import edu.tum.sse.jtec.util.JSONUtils;

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
        registerCoverage(className + "#" + methodSignature + returnType);
    }

    private void registerCoverage(String value) {
        coverageMap.put(value);
    }

    public void clearCoverage() {
        coverageMap.clear();
    }

    public CoverageMap getCoverageMap() {
        return coverageMap;
    }

    public void registerDump(final String dumpId) {
        coverageMap.dump(dumpId);
    }

    public void saveCoverage(final String outputPath) throws IOException {
        final String json = JSONUtils.toJson(coverageMap.getCollectedProbes());
        final Path outputFile = Paths.get(outputPath);
        IOUtils.createFileAndEnclosingDir(outputFile);
        IOUtils.appendToFile(outputFile, json, true);
        clearCoverage();
    }
}
