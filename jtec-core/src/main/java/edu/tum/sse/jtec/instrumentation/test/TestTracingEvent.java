package edu.tum.sse.jtec.instrumentation.test;

public enum TestTracingEvent {
    SUITE_STARTED("Test suite started"),
    TEST_STARTED("Test started"),
    TEST_FINISHED("Test finished"),
    SUITE_FINISHED("Test suite finished");

    private final String event;

    TestTracingEvent(final String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return event;
    }
}
