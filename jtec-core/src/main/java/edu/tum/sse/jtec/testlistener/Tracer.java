package edu.tum.sse.jtec.testlistener;

import net.bytebuddy.agent.builder.ResettableClassFileTransformer;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

public class Tracer {
    private final Instrumentation instrumentation;
    private final List<ResettableClassFileTransformer> transformers;

    public Tracer(Instrumentation instrumentation, AgentOptions options) {
        this.instrumentation = instrumentation;
        this.transformers = new ArrayList<>();
    }
}
