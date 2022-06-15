package edu.tum.sse.jtec.instrumentation.testevent;

import edu.tum.sse.jtec.instrumentation.AbstractInstrumentation;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.ExecutionFinishedInterceptor;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.ExecutionStartedInterceptor;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.TestEndInterceptor;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.TestRunFinishedInterceptor;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.TestRunStartedInterceptor;
import edu.tum.sse.jtec.instrumentation.testevent.interceptors.TestStartInterceptor;
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
    public static final String TEST_EXECUTION_LISTENER_SPRING = "org.springframework.test.context.TestExecutionListener";
    // public static final String TEST_RESULT = "junit.framework.TestResult";
    // junit 5 org.junit.platform.engine.support.hierarchicalNode might be an option

    // TODO use RunNotifier instead of runListener perhaps
    public static final String TEST_STARTED = "testStarted";
    public static final String TEST_ENDED = "testFinished";
    public static final String EXECUTION_STARTED = "executionStarted";
    public static final String EXECUTION_FINISHED = "executionFinished";
    public static final String TEST_RUN_STARTED = "testRunStarted";
    public static final String TEST_RUN_FINISHED = "testRunFinished";

    private Instrumentation instrumentation;
    private ResettableClassFileTransformer transformer;

    public TestEventInstrumentation(final String outputPath) {
        super(outputPath);
    }

    private static AgentBuilder.Transformer testEventTransformer() {
        return (builder, typeDescription, classLoader, module) ->
                builder.visit(Advice.withCustomMapping()
                                .to(TestStartInterceptor.class)
                                .on(ElementMatchers.nameMatches(TEST_STARTED)))
                        .visit(Advice.withCustomMapping()
                                .to(TestEndInterceptor.class)
                                .on(ElementMatchers.nameMatches(TEST_ENDED)))
                        .visit(Advice.withCustomMapping()
                                .to(ExecutionStartedInterceptor.class)
                                .on(ElementMatchers.nameMatches(EXECUTION_STARTED)))
                        .visit(Advice.withCustomMapping()
                                .to(ExecutionFinishedInterceptor.class)
                                .on(ElementMatchers.nameMatches(EXECUTION_FINISHED)))
                        .visit(Advice.withCustomMapping()
                                .to(TestRunStartedInterceptor.class)
                                .on(ElementMatchers.nameMatches(TEST_RUN_STARTED)))
                        .visit(Advice.withCustomMapping()
                                .to(TestRunFinishedInterceptor.class)
                                .on(ElementMatchers.nameMatches(TEST_RUN_FINISHED)));
    }

    @Override
    public TestEventInstrumentation attach(final Instrumentation instrumentation, final File tempFolder) {
        this.instrumentation = instrumentation;
        TestEventInterceptorUtility.testingLogFilePath = outputPath;
        transformer = new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemError())
                .with(AgentBuilder.Listener.StreamWriting.toSystemError().withTransformationsOnly())
                .ignore(ElementMatchers.nameStartsWith(BYTEBUDDY_PACKAGE))
                .ignore(ElementMatchers.nameStartsWith(JTEC_PACKAGE))
                .with(AgentBuilder.InstallationListener.StreamWriting.toSystemError())
                .type(ElementMatchers.nameMatches(RUN_LISTENER_JUNIT4)
                        .or(ElementMatchers.hasSuperType(ElementMatchers.nameMatches(TEST_EXECUTION_LISTENER_JUNIT5)))
                        .or(ElementMatchers.hasSuperType(ElementMatchers.nameMatches(TEST_EXECUTION_LISTENER_SPRING))))
                .transform(testEventTransformer()).installOn(instrumentation);
        return this;
    }

    @Override
    public void reset() {
        if (instrumentation != null && transformer != null) {
            instrumentation.removeTransformer(transformer);
        }
    }
}
