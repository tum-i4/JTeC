package edu.tum.sse.jtec.instrumentation.coverage;

import edu.tum.sse.jtec.instrumentation.AbstractInstrumentation;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

/**
 * Adds coverage instrumentation at desired granularity (e.g., class-level).
 */
public class CoverageInstrumentation extends AbstractInstrumentation<CoverageInstrumentation> {

    private final CoverageLevel coverageLevel;
    private final String includePattern;
    private final String excludePattern;
    private final boolean shouldInstrument;

    private Instrumentation instrumentation;
    private ClassFileTransformer transformer;
    private CoverageMonitor coverageMonitor;

    public CoverageInstrumentation(final String outputPath,
                                   final CoverageLevel coverageLevel,
                                   final String includePattern,
                                   final String excludePattern,
                                   final boolean shouldInstrument) {
        super(outputPath);
        this.coverageLevel = coverageLevel;
        this.includePattern = includePattern;
        this.excludePattern = excludePattern;
        this.shouldInstrument = shouldInstrument;
    }

    @Override
    public void reset() {
        if (instrumentation != null && transformer != null) {
            instrumentation.removeTransformer(transformer);
        }
    }

    @Override
    public CoverageInstrumentation attach(final Instrumentation instrumentation, final File tempFolder) {
        this.instrumentation = instrumentation;
        coverageMonitor = CoverageMonitor.create();
        GlobalCoverageMonitor.set(coverageMonitor);
        if (shouldInstrument) {
            transformer = new AgentBuilder.Default()
                    .disableClassFormatChanges()
                    .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                    .with(new AgentBuilder.InjectionStrategy.UsingInstrumentation(instrumentation, tempFolder))
                    .type(ElementMatchers.nameMatches(includePattern))
                    .transform(getCoverageTransformer())
                    .ignore(ElementMatchers.nameMatches(excludePattern))
                    .installOn(instrumentation);
        } else {
            // In case we do not instrument, we can still track all loaded class files without modifying them.
            transformer = new LoadedClassFileMonitor(includePattern, excludePattern);
            instrumentation.addTransformer(transformer);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::dumpCoverage));
        return this;
    }

    private AgentBuilder.Transformer getCoverageTransformer() {
        return CoverageTransformer.create(coverageLevel);
    }

    public void dumpCoverage() {
        try {
            coverageMonitor.dumpCoverage(outputPath);
        } catch (final Exception exception) {
            System.err.println("Failed to dump coverage: " + exception.getMessage());
        }
    }

}
