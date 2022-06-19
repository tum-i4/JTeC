package edu.tum.sse.jtec.instrumentation.systemevent;

import edu.tum.sse.jtec.instrumentation.AbstractInstrumentation;
import edu.tum.sse.jtec.instrumentation.systemevent.interceptors.ClassLoaderInterceptor;
import edu.tum.sse.jtec.instrumentation.systemevent.interceptors.PathInterceptor;
import edu.tum.sse.jtec.instrumentation.systemevent.interceptors.ProcessStartInterceptor;
import edu.tum.sse.jtec.instrumentation.systemevent.interceptors.SocketInterceptor;
import edu.tum.sse.jtec.instrumentation.systemevent.interceptors.StringPathInterceptor;
import edu.tum.sse.jtec.instrumentation.systemevent.interceptors.ThreadStartInterceptor;
import edu.tum.sse.jtec.util.IOUtils;
import edu.tum.sse.jtec.util.JSONUtils;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.agent.builder.ResettableClassFileTransformer;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.BYTEBUDDY_PACKAGE;
import static edu.tum.sse.jtec.instrumentation.InstrumentationUtils.JTEC_PACKAGE;

/**
 * Adds system event instrumentation for files, resources (from JARs), libraries (e.g., native DLLs), sockets, threads, and processes.
 */
public class SystemEventInstrumentation extends AbstractInstrumentation<SystemEventInstrumentation> {

    private static final String TYPES_TO_TRACE = "(" +
            "java.io.FileInputStream|" +
            "java.io.FileOutputStream|" +
            "sun.nio.fs.UnixFileSystemProvider|" +
            "java.nio.file.spi.FileSystemProvider|" +
            "sun.nio.fs.WindowsFileSystemProvider|" +
            "java.io.RandomAccessFile|" +
            "java.lang.ClassLoader|" +
            "java.net.Socket|" +
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
    private final String fileIncludes;
    private final String fileExcludes;
    private Instrumentation instrumentation;
    private ResettableClassFileTransformer transformer;

    public SystemEventInstrumentation(final String outputPath,
                                      final String fileIncludes,
                                      final String fileExcludes
    ) {
        super(outputPath);
        this.fileIncludes = fileIncludes;
        this.fileExcludes = fileExcludes;
    }

    @Override
    public SystemEventInstrumentation attach(final Instrumentation instrumentation, final File tempFolder) {
        this.instrumentation = instrumentation;
        this.transformer = new AgentBuilder.Default()
                .disableClassFormatChanges()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemError())
                .with(AgentBuilder.Listener.StreamWriting.toSystemError().withTransformationsOnly())
                .with(new AgentBuilder.InjectionStrategy.UsingInstrumentation(instrumentation, tempFolder))
                .ignore(ElementMatchers.nameStartsWith(BYTEBUDDY_PACKAGE))
                .ignore(ElementMatchers.nameStartsWith(JTEC_PACKAGE))
                .with(AgentBuilder.InstallationListener.StreamWriting.toSystemError())
                .type(ElementMatchers.nameMatches(TYPES_TO_TRACE))
                .transform(systemEventTransformer()).installOn(instrumentation);

        Runtime.getRuntime().addShutdownHook(new Thread(this::dumpEvents));

        return this;
    }

    public void dumpEvents() {
        try {
            Pattern fileIncludes = Pattern.compile(this.fileIncludes);
            Pattern fileExcludes = Pattern.compile(this.fileExcludes);
            List<SystemInstrumentationEvent> events = SystemEventMonitor.getEvents()
                    .stream()
                    .filter(event -> {
                        if (event.getAction() != SystemInstrumentationEvent.Action.OPEN || (event.getTarget() != SystemInstrumentationEvent.Target.FILE && event.getTarget() != SystemInstrumentationEvent.Target.RESOURCE)) {
                            return true;
                        }
                        return fileIncludes.matcher(event.getValue()).matches() && !fileExcludes.matcher(event.getValue()).matches();
                    })
                    .collect(Collectors.toList());
            final String json = JSONUtils.toJson(events);
            final Path outputFile = Paths.get(outputPath);
            IOUtils.createFileAndEnclosingDir(outputFile);
            IOUtils.appendToFile(outputFile, json, true);
        } catch (final Exception exception) {
            System.err.println("Failed to dump events: " + exception.getMessage());
        }
    }

    @Override
    public void reset() {
        if (instrumentation != null && transformer != null) {
            instrumentation.removeTransformer(transformer);
        }
    }

    /**
     * Adds visitors to the methods for tracing system events.
     */
    private AgentBuilder.Transformer systemEventTransformer() {
        return (builder, typeDescription, classLoader, module) ->
                builder.visit(Advice.withCustomMapping()
                                .to(StringPathInterceptor.class)
                                .on(ElementMatchers.nameMatches(STRING_PARAMETER_TRACED_METHODS)
                                        .and(ElementMatchers.takesArgument(0, TypeDescription.STRING)))).
                        visit(Advice.withCustomMapping()
                                .to(SocketInterceptor.class)
                                .on(ElementMatchers.nameMatches(SOCKET_ADDRESS_PARAMETER_TRACED_METHODS).and(ElementMatchers.takesArguments(2)))).
                        visit(Advice.withCustomMapping()
                                .to(PathInterceptor.class)
                                .on(ElementMatchers.nameMatches(PATH_PARAMETER_TRACED_METHODS).and(ElementMatchers.takesArgument(0, Path.class)))).
                        visit(Advice.withCustomMapping()
                                .to(ClassLoaderInterceptor.class)
                                .on(ElementMatchers.nameMatches(CLASS_LOADER_TRACED_METHODS).and(ElementMatchers.takesArgument(0, TypeDescription.STRING)))).
                        visit(Advice.withCustomMapping()
                                .to(ThreadStartInterceptor.class)
                                .on(ElementMatchers.nameMatches(THREAD_CREATION_METHODS).and(ElementMatchers.takesArguments(0).and(ElementMatchers.returns(TypeDescription.VOID))))).
                        visit(Advice.withCustomMapping()
                                .to(ProcessStartInterceptor.class)
                                .on(ElementMatchers.nameMatches(PROCESS_CREATION_METHODS).and(ElementMatchers.returns(Process.class).and(ElementMatchers.takesArguments(1)))));
    }
}
