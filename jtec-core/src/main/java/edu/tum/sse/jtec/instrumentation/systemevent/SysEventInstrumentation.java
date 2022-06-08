package edu.tum.sse.jtec.instrumentation.systemevent;

import edu.tum.sse.jtec.instrumentation.AbstractInstrumentation;
import edu.tum.sse.jtec.instrumentation.systemevent.interceptors.*;
import edu.tum.sse.jtec.util.ProcessUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;
import java.nio.file.Path;

import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.*;

/**
 * Adds system event instrumentation for files, resources (from JARs), libraries (e.g., native DLLs), sockets, threads, and processes.
 */
public class SysEventInstrumentation extends AbstractInstrumentation<SysEventInstrumentation> {

    private static final String TYPES_TO_TRACE = "(" +
            "java.io.FileInputStream|" +
            "java.io.FileOutputStream|" +
            "sun.nio.fs.UnixFileSystemProvider|" +
            "java.nio.file.spi.FileSystemProvider|" +
            "sun.nio.fs.WindowsFileSystemProvider|" +
            "java.io.RandomAccessFile|" +
            "java.net.Socket|" +
            "java.lang.ClassLoader|" +
            "java.lang.Thread|" +
            "java.lang.ProcessBuilder" +
            ")";
    private static final String CLASS_LOADER_TRACED_METHODS = "(" +
            "getResource|" +
            "findLibrary" +
            ")";
    private static final String STRING_PARAMETER_TRACED_METHODS = "open";
    private static final String SOCKET_ADDRESS_PARAMETER_TRACED_METHODS = "connect";
    private static final String PATH_PARAMETER_TRACED_METHODS = "(" +
            "newFileChannel|" +
            "newAsynchronousFileChannel|" +
            "newByteChannel|" +
            "copy|" +
            "move" +
            ")";
    private static final String THREAD_CREATION_METHODS = "start";
    private static final String PROCESS_CREATION_METHODS = "start";

    private Instrumentation instrumentation;
    private ResettableClassFileTransformer transformer;

    public SysEventInstrumentation(final String outputPath) {
        super(outputPath);
    }

    @Override
    public SysEventInstrumentation attach(final Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
        this.transformer = new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemError())
                .with(AgentBuilder.Listener.StreamWriting.toSystemError().withTransformationsOnly())
                .ignore(ElementMatchers.nameStartsWith(BYTEBUDDY_PACKAGE))
                .ignore(ElementMatchers.nameStartsWith(JTEC_PACKAGE))
                .with(AgentBuilder.InstallationListener.StreamWriting.toSystemError())
                .type(ElementMatchers.nameMatches(TYPES_TO_TRACE))
                .transform(systemEventTransformer()).installOn(instrumentation);
        return this;
    }

    public void reset() {
        if (instrumentation != null && transformer != null) {
            instrumentation.removeTransformer(transformer);
        }
    }

    /**
     * Adds visitors to the methods for tracing system events.
     */
    private AgentBuilder.Transformer systemEventTransformer() {
        final String currentPid = ProcessUtils.getCurrentPid();
        return (builder, typeDescription, classLoader, module) ->
                builder.visit(Advice.withCustomMapping().bind(AdviceOutput.class, outputPath).bind(AdvicePid.class, currentPid)
                                .to(StringPathInterceptor.class)
                                .on(ElementMatchers.nameMatches(STRING_PARAMETER_TRACED_METHODS)
                                        .and(ElementMatchers.takesArgument(0, TypeDescription.STRING)))).
                        visit(Advice.withCustomMapping().bind(AdviceOutput.class, outputPath).bind(AdvicePid.class, currentPid)
                                .to(SocketInterceptor.class)
                                .on(ElementMatchers.nameMatches(SOCKET_ADDRESS_PARAMETER_TRACED_METHODS).and(ElementMatchers.takesArguments(2)))).
                        visit(Advice.withCustomMapping().bind(AdviceOutput.class, outputPath).bind(AdvicePid.class, currentPid)
                                .to(PathInterceptor.class)
                                .on(ElementMatchers.nameMatches(PATH_PARAMETER_TRACED_METHODS).and(ElementMatchers.takesArgument(0, Path.class)))).
                        visit(Advice.withCustomMapping().bind(AdviceOutput.class, outputPath).bind(AdvicePid.class, currentPid)
                                .to(ClassLoaderInterceptor.class)
                                .on(ElementMatchers.nameMatches(CLASS_LOADER_TRACED_METHODS))).
                        visit(Advice.withCustomMapping().bind(AdviceOutput.class, outputPath).bind(AdvicePid.class, currentPid)
                                .to(ThreadStartInterceptor.class)
                                .on(ElementMatchers.nameMatches(THREAD_CREATION_METHODS).and(ElementMatchers.takesArguments(0).and(ElementMatchers.returns(TypeDescription.VOID))))).
                        visit(Advice.withCustomMapping().bind(AdviceOutput.class, outputPath).bind(AdvicePid.class, currentPid)
                                .to(ProcessStartInterceptor.class)
                                .on(ElementMatchers.nameMatches(PROCESS_CREATION_METHODS).and(ElementMatchers.returns(Process.class).and(ElementMatchers.takesArguments(1)))));
    }
}
