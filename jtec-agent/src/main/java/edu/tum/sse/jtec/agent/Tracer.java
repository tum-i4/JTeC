package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instrumentation.AbstractInstrumentation;
import edu.tum.sse.jtec.instrumentation.coverage.CoverageInstrumentation;
import edu.tum.sse.jtec.instrumentation.systemevent.SysEventInstrumentation;
import edu.tum.sse.jtec.instrumentation.testevent.TestEventInstrumentation;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.getCurrentPid;
import static edu.tum.sse.jtec.util.IOUtils.createFileAndEnclosingDir;

/**
 * A {@link Tracer} registers transformers that perform bytecode instrumentation based on {@link AgentOptions}.
 */
public class Tracer {
    private final Instrumentation instrumentation;
    private final List<AbstractInstrumentation> customInstrumentationList;

    public Tracer(final Instrumentation instrumentation, final AgentOptions options) {
        this.instrumentation = instrumentation;
        customInstrumentationList = new ArrayList<>();
        // Order matters here: (1) test events, (2) system, (3) coverage
        if (options.shouldTraceTestEvents()) {
            Path testEventOutput = options.getOutputPath().resolve(String.format("%s_%d_test.log", getCurrentPid(), System.currentTimeMillis()));
            createFileAndEnclosingDir(testEventOutput);
            customInstrumentationList.add(new TestEventInstrumentation(testEventOutput.toString()).attach(instrumentation));
        }
        if (options.shouldTraceSystemEvents()) {
            Path sysEventOutput = options.getOutputPath().resolve(String.format("%s_%d_sys.log", getCurrentPid(), System.currentTimeMillis()));
            createFileAndEnclosingDir(sysEventOutput);
            customInstrumentationList.add(new SysEventInstrumentation(sysEventOutput.toString()).attach(instrumentation));
        }
        if (options.shouldTraceCoverage()) {
            Path covEventOutput = options.getOutputPath().resolve(String.format("%s_%d_cov.log", getCurrentPid(), System.currentTimeMillis()));
            createFileAndEnclosingDir(covEventOutput);
            customInstrumentationList.add(
                    new CoverageInstrumentation(
                            covEventOutput.toString(),
                            options.getCoverageLevel(),
                            options.getCoverageIncludes(),
                            options.getCoverageExcludes(),
                            options.shouldInstrumentCoverage()
                    ).attach(instrumentation));
        }
    }
}
