package edu.tum.sse.jtec.instrumentation.testevent;

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

import java.lang.instrument.Instrumentation;

import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.BYTEBUDDY_PACKAGE;
import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.JTEC_PACKAGE;

public class TestEventInstrumentation {

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

    public static ResettableClassFileTransformer attachTracer(final Instrumentation instrumentation, final String outputPath) {
        TestEventInterceptorUtility.testingLogFilePath = outputPath;
        return new AgentBuilder.Default()
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
                .transform(systemEventTransformer()).installOn(instrumentation);
    }

    /**
     * Adds visitors to the methods for tracing system events.
     */
    private static AgentBuilder.Transformer systemEventTransformer() {
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
}
