package edu.tum.sse.jtec.instrumentation.testevent;

import edu.tum.sse.jtec.instrumentation.AbstractInstrumentation;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.JUnit4TestEndInterceptor;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.JUnit4TestFailedInterceptor;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.JUnit4TestIgnoredInterceptor;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.JUnit4TestRunFinishedInterceptor;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.JUnit4TestStartInterceptor;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.JUnit5ExecutionFinishedInterceptor;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.JUnit5ExecutionStartedInterceptor;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.lang.instrument.Instrumentation;

import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.BYTEBUDDY_PACKAGE;
import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.JTEC_PACKAGE;

/**
 * Adds test event instrumentation for the JUnit testing framework.
 */
public class TestEventInstrumentation extends AbstractInstrumentation<TestEventInstrumentation> {

    public static final String RUN_LISTENER_JUNIT4 = "org.junit.runner.notification.RunListener";
    public static final String TEST_EXECUTION_LISTENER_JUNIT5 = "org.junit.platform.launcher.TestExecutionListener";
    // public static final String TEST_RESULT = "junit.framework.TestResult";
    // junit 5 org.junit.platform.engine.support.hierarchicalNode might be an option

    // TODO use RunNotifier instead of runListener perhaps
    public static final String TEST_STARTED = "testStarted";
    public static final String TEST_FINISHED = "testFinished";
    public static final String EXECUTION_STARTED = "executionStarted";
    public static final String EXECUTION_FINISHED = "executionFinished";
    public static final String TEST_FAILED = "testFailure";
    public static final String TEST_IGNORED = "testIgnored";
    public static final String TEST_RUN_FINISHED = "testRunFinished";
    private final boolean shouldInstrument;

    private Instrumentation instrumentation;
    private ResettableClassFileTransformer transformer;

    public TestEventInstrumentation(final String outputPath, final boolean shouldInstrument) {
        super(outputPath);
        this.shouldInstrument = shouldInstrument;
    }

    private static AgentBuilder.Transformer testEventTransformer() {
        return (builder, typeDescription, classLoader, module) ->
                builder.visit(Advice.withCustomMapping()
                                .to(JUnit4TestStartInterceptor.class)
                                .on(ElementMatchers.nameMatches(TEST_STARTED)))
                        .visit(Advice.withCustomMapping()
                                .to(JUnit4TestEndInterceptor.class)
                                .on(ElementMatchers.nameMatches(TEST_FINISHED)))
                        .visit(Advice.withCustomMapping()
                                .to(JUnit5ExecutionStartedInterceptor.class)
                                .on(ElementMatchers.nameMatches(EXECUTION_STARTED)))
                        .visit(Advice.withCustomMapping()
                                .to(JUnit5ExecutionFinishedInterceptor.class)
                                .on(ElementMatchers.nameMatches(EXECUTION_FINISHED)))
                        .visit(Advice.withCustomMapping()
                                .to(JUnit4TestFailedInterceptor.class)
                                .on(ElementMatchers.nameMatches(TEST_FAILED)))
                        .visit(Advice.withCustomMapping()
                                .to(JUnit4TestIgnoredInterceptor.class)
                                .on(ElementMatchers.nameMatches(TEST_IGNORED)))
                        .visit(Advice.withCustomMapping()
                                .to(JUnit4TestRunFinishedInterceptor.class)
                                .on(ElementMatchers.nameMatches(TEST_RUN_FINISHED)));
    }

    @Override
    public TestEventInstrumentation attach(final Instrumentation instrumentation, final File tempFolder) {
        this.instrumentation = instrumentation;
        TestEventInterceptorUtility.testingLogFilePath = outputPath;
        TestEventInterceptorUtility.testEventInstrumentation = shouldInstrument;
        if (shouldInstrument) {
            transformer = new AgentBuilder.Default()
                    .disableClassFormatChanges()
                    .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                    .with(new AgentBuilder.InjectionStrategy.UsingInstrumentation(instrumentation, tempFolder))
                    .ignore(ElementMatchers.nameStartsWith(BYTEBUDDY_PACKAGE))
                    .ignore(ElementMatchers.nameStartsWith(JTEC_PACKAGE))
                    .type(ElementMatchers.nameMatches(RUN_LISTENER_JUNIT4)
                            .or(ElementMatchers.hasSuperType(ElementMatchers.nameMatches(RUN_LISTENER_JUNIT4)))
                            .or(ElementMatchers.hasSuperType(ElementMatchers.nameMatches(TEST_EXECUTION_LISTENER_JUNIT5))))
                    .transform(testEventTransformer())
                    .installOn(instrumentation);
        }

        return this;
    }

    @Override
    public void reset() {
        if (instrumentation != null && transformer != null) {
            instrumentation.removeTransformer(transformer);
        }
    }
}
