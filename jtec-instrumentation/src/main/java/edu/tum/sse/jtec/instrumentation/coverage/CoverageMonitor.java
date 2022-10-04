package edu.tum.sse.jtec.instrumentation.coverage;

import edu.tum.sse.jtec.util.IOUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CoverageMonitor {
    private final CoverageMap coverageMap = new CoverageMap();
    private final CoverageProbeFactory coverageProbeFactory;

    private CoverageMonitor(final CoverageProbeFactory coverageProbeFactory) {
        this.coverageProbeFactory = coverageProbeFactory;
    }

    public static CoverageMonitor create(final CoverageProbeFactory coverageProbeFactory) {
        return new CoverageMonitor(coverageProbeFactory);
    }

    public void registerClass(final String className) {
        registerProbe(coverageProbeFactory.createClassProbe(className));
    }

    public void registerMethodCall(final String className, final String methodSignature, final String returnType) {
        registerProbe(coverageProbeFactory.createMethodProbe(className, methodSignature, returnType));
    }

    private void registerProbe(final CoverageProbe probe) {
        if (probe != null && !coverageMap.contains(probe.getCoverageRunId(), probe.getProbeId())) {
            coverageMap.put(probe.getCoverageRunId(), probe.getProbeId());
        }
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
