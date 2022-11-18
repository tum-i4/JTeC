package edu.tum.sse.jtec.instrumentation.coverage;

public final class CoverageDumpStrategy {

    private static final CoverageDumpStrategy instance = new CoverageDumpStrategy();
    private CoverageDumpTrigger trigger = CoverageDumpTrigger.PER_PROCESS;

    public static CoverageDumpStrategy getInstance() {
        return instance;
    }

    public void setStrategy(final CoverageDumpTrigger trigger) {
        this.trigger = trigger;
    }

    public boolean isReusingForks() {
        return trigger == CoverageDumpTrigger.PER_TEST;
    }

    public enum CoverageDumpTrigger {
        PER_PROCESS,
        PER_TEST
    }

    public enum TestPhaseDump {
        GLOBAL_SETUP("GLOBAL_SETUP"),
        GLOBAL_TEARDOWN("GLOBAL_TEARDOWN");

        private final String phase;

        TestPhaseDump(final String phase) {
            this.phase = phase;
        }

        @Override
        public String toString() {
            return phase;
        }
    }
}
