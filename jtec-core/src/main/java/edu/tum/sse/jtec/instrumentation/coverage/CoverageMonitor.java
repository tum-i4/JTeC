package edu.tum.sse.jtec.instrumentation.coverage;

import com.google.gson.Gson;

import java.io.IOException;

import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.writeToFileLocking;

public class CoverageMonitor {
    private final CoverageMap coverageMap = new CoverageMap();
    private final CoverageProbeFactory coverageProbeFactory;

    public CoverageMonitor(CoverageProbeFactory coverageProbeFactory) {
        this.coverageProbeFactory = coverageProbeFactory;
    }

    public void registerClass(String className) {
        registerProbe(coverageProbeFactory.createClassProbe(className));
    }

    public void registerMethodCall(String className, String methodSignature, String returnType) {
        registerProbe(coverageProbeFactory.createMethodProbe(className, methodSignature, returnType));
    }

    private void registerProbe(CoverageProbe probe) {
        if (!coverageMap.contains(probe.getCoverageRunId(), probe.getProbeId())) {
            coverageMap.put(probe.getCoverageRunId(), probe.getProbeId());
        }
    }

    public void clearCoverage() {
        coverageMap.clear();
    }

    public void dumpCoverage(String outputPath) throws IOException {
        String json = new Gson().toJson(coverageMap.getCollectedProbes());
        writeToFileLocking(outputPath, json);
        clearCoverage();
    }
}
