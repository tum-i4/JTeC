package edu.tum.sse.jtec.instrumentation.coverage;

public final class TestIdStrategy implements CoverageIdStrategy {

    private static final TestIdStrategy instance = new TestIdStrategy();
    /**
     * The test identifier can either be a test suite/case name or
     * one of the other phases of a test suite execution
     */
    private String testId = TestPhaseId.GLOBAL_SETUP.phase;

    public static TestIdStrategy getInstance() {
        return instance;
    }

    public void setTestId(final String testId) {
        this.testId = testId;
    }

    @Override
    public String getId() {
        return testId;
    }

    public enum TestPhaseId {
        GLOBAL_SETUP("GLOBAL_SETUP"),
        GLOBAL_TEARDOWN("GLOBAL_TEARDOWN");

        private final String phase;

        TestPhaseId(final String phase) {
            this.phase = phase;
        }

        @Override
        public String toString() {
            return phase;
        }
    }
}
