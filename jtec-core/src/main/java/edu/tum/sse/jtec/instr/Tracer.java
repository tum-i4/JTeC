package edu.tum.sse.jtec.instr;

import edu.tum.sse.jtec.instr.sysevent.SysEventInstrumentation;
import edu.tum.sse.jtec.instr.testevent.TestEventInstrumentation;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Tracer} registers transformers that perform bytecode instrumentation based on {@link AgentOptions}.
 */
public class Tracer {
    private final Instrumentation instrumentation;
    private final List<ResettableClassFileTransformer> transformers;

    public Tracer(final Instrumentation instrumentation, final AgentOptions options) {
        this.instrumentation = instrumentation;
        this.transformers = new ArrayList<>();
        if (options.shouldTraceTestEvents()) {
            transformers.add(TestEventInstrumentation.attachTracer(instrumentation, options.getTestEventOutputPath().toString()));
        }
        if (options.shouldTraceSystemEvents()) {
            transformers.add(SysEventInstrumentation.attachTracer(instrumentation, options.getSystemEventOutputPath().toString()));
        }
    }
}
