package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instrumentation.AbstractInstrumentation;
import edu.tum.sse.jtec.instrumentation.coverage.CoverageInstrumentation;
import edu.tum.sse.jtec.instrumentation.systemevent.SysEventInstrumentation;
import edu.tum.sse.jtec.instrumentation.testevent.TestEventInstrumentation;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Tracer} registers transformers that perform bytecode instrumentation based on {@link AgentOptions}.
 */
public class Tracer {
    private final Instrumentation instrumentation;
    private final List<AbstractInstrumentation> customInstrumentationList;

    public Tracer(final Instrumentation instrumentation, final AgentOptions options) {
        this.instrumentation = instrumentation;
        customInstrumentationList = new ArrayList<>();
        if (options.shouldTraceTestEvents()) {
            customInstrumentationList.add(new TestEventInstrumentation(options.getTestEventOutputPath().toString()).attach(instrumentation));
        }
        if (options.shouldTraceSystemEvents()) {
            customInstrumentationList.add(new SysEventInstrumentation(options.getSystemEventOutputPath().toString()).attach(instrumentation));
        }
        if (options.shouldTraceCoverage()) {
            customInstrumentationList.add(
                    new CoverageInstrumentation(
                            options.getCoverageOutputPath().toString(),
                            options.getCoverageLevel(),
                            options.getCoverageIncludes(),
                            options.getCoverageExcludes(),
                            options.shouldInstrumentCoverage()
                    ).attach(instrumentation));
        }
    }
}
