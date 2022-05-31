package edu.tum.sse.jtec.testlistener;

import edu.tum.sse.jtec.testlistener.testRunInstrumentation.TestEventInstrumentation;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class Tracer {
    private final Instrumentation instrumentation;
    private final List<ResettableClassFileTransformer> transformers;

    public Tracer(final Instrumentation instrumentation, final AgentOptions options) {
        this.instrumentation = instrumentation;
        this.transformers = new ArrayList<>();
        if (options.getTraceTestEvents()) {
            transformers.add(TestEventInstrumentation.attachTracer(instrumentation, options.getTestEventOutputPath().toString()));
        }
    }
}
