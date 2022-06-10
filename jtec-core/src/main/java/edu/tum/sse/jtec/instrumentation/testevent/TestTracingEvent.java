package edu.tum.sse.jtec.instrumentation.testevent;

public enum TestTracingEvent {
    SUITE_STARTED("SUITE_STARTED"),
    TEST_STARTED("TEST_STARTED"),
    TEST_FINISHED("TEST_FINISHED"),
    SUITE_FINISHED("SUITE_FINISHED");

    private final String event;

    TestTracingEvent(final String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return event;
    }
}
