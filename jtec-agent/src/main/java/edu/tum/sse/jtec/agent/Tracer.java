package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instrumentation.AbstractInstrumentation;
import edu.tum.sse.jtec.instrumentation.system.SysEventInstrumentation;
import edu.tum.sse.jtec.instrumentation.test.TestEventInstrumentation;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Tracer} registers transformers that perform bytecode instrumentation based on {@link AgentOptions}.
 */
public class Tracer {
    private final Instrumentation instrumentation;
    private final List<AbstractInstrumentation> transformers;

    public Tracer(final Instrumentation instrumentation, final AgentOptions options) {
        this.instrumentation = instrumentation;
        transformers = new ArrayList<>();
        if (options.shouldTraceTestEvents()) {
            transformers.add(new TestEventInstrumentation(options.getTestEventOutputPath().toString()).attach(instrumentation));
        }
        if (options.shouldTraceSystemEvents()) {
            transformers.add(new SysEventInstrumentation(options.getSystemEventOutputPath().toString()).attach(instrumentation));
        }
        // TODO: add coverage instrumentation
    }
}
