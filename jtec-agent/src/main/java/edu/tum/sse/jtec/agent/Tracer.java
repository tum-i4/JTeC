package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instrumentation.system.SysEventInstrumentation;
import edu.tum.sse.jtec.instrumentation.test.TestEventInstrumentation;

import java.lang.instrument.Instrumentation;

/**
 * A {@link Tracer} registers transformers that perform bytecode instrumentation based on {@link AgentOptions}.
 */
public class Tracer {
    private final Instrumentation instrumentation;

    public Tracer(final Instrumentation instrumentation, final AgentOptions options) {
        this.instrumentation = instrumentation;
        if (options.shouldTraceTestEvents()) {
            TestEventInstrumentation.attachTracer(instrumentation, options.getTestEventOutputPath().toString());
        }
        if (options.shouldTraceSystemEvents()) {
            SysEventInstrumentation.attachTracer(instrumentation, options.getSystemEventOutputPath().toString());
        }
        // TODO: add coverage instrumentation
    }
}
