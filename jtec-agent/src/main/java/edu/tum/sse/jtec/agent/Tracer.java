package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instrumentation.AbstractInstrumentation;
import edu.tum.sse.jtec.instrumentation.coverage.CoverageInstrumentation;
import edu.tum.sse.jtec.instrumentation.systemevent.SysEventInstrumentation;
import edu.tum.sse.jtec.instrumentation.testevent.TestEventInstrumentation;
import edu.tum.sse.jtec.util.ProcessUtils;

import java.lang.instrument.Instrumentation;
import java.util.*;

/**
 * A {@link Tracer} registers transformers that perform bytecode instrumentation based on {@link AgentOptions}.
 */
public class Tracer {

    public static final String PID_KEY = "PID";
    private final Instrumentation instrumentation;
    private final List<AbstractInstrumentation> customInstrumentationList;

    public Tracer(final Instrumentation instrumentation, final AgentOptions options) {
        // Before we start tracing, we need to run the pre-test command, if specified.
        if (!options.getPreTestCommand().isEmpty()) {
            try {
                ProcessUtils.run(options.getPreTestCommand(), Collections.singletonMap(PID_KEY, ProcessUtils.getCurrentPid()), false);
            } catch (Exception e) {
                System.err.println("Failed to run pre-test command " + options.getPreTestCommand() + " : " + e.getMessage());
            }
        }

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
