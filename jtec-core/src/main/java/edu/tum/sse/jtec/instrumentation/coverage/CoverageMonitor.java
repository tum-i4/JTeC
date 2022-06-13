package edu.tum.sse.jtec.instrumentation.coverage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static edu.tum.sse.jtec.util.IOUtils.appendToFile;
import static edu.tum.sse.jtec.util.IOUtils.createFileAndEnclosingDir;
import static edu.tum.sse.jtec.util.JSONUtils.toJson;

public class CoverageMonitor {
    private final CoverageMap coverageMap = new CoverageMap();
    private final CoverageProbeFactory coverageProbeFactory;

    private CoverageMonitor(CoverageProbeFactory coverageProbeFactory) {
        this.coverageProbeFactory = coverageProbeFactory;
    }

    public static CoverageMonitor create(CoverageProbeFactory coverageProbeFactory) {
        return new CoverageMonitor(coverageProbeFactory);
    }

    public void registerClass(String className) {
        registerProbe(coverageProbeFactory.createClassProbe(className));
    }

    public void registerMethodCall(String className, String methodSignature, String returnType) {
        registerProbe(coverageProbeFactory.createMethodProbe(className, methodSignature, returnType));
    }

    private void registerProbe(CoverageProbe probe) {
        if (probe != null && !coverageMap.contains(probe.getCoverageRunId(), probe.getProbeId())) {
            coverageMap.put(probe.getCoverageRunId(), probe.getProbeId());
        }
    }

    public void clearCoverage() {
        coverageMap.clear();
    }

    public void dumpCoverage(String outputPath) throws IOException {
        String json = toJson(coverageMap.getCollectedProbes());
        Path outputFile = Paths.get(outputPath);
        createFileAndEnclosingDir(outputFile);
        appendToFile(outputFile, json, true);
        clearCoverage();
    }
}
