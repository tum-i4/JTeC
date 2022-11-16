package edu.tum.sse.jtec.instrumentation.coverage;

import edu.tum.sse.jtec.util.ProcessUtils;

public final class PIDStrategy implements CoverageIdStrategy {
    private static final String pid = ProcessUtils.getCurrentPid();

    private static final PIDStrategy instance = new PIDStrategy();

    public static PIDStrategy getInstance() {
        return instance;
    }

    @Override
    public String getId() {
        return pid;
    }
}
