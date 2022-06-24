package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.SystemEventMonitor;
import edu.tum.sse.jtec.instrumentation.systemevent.SystemInstrumentationEvent;
import net.bytebuddy.asm.Advice;

public class ClassLoaderInterceptor {
    /**
     * Writes the given path to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final String printedName) {
        String lowerCasedName = printedName.toLowerCase();
        // If loading native libraries from the JVM, the extension often gets lost which makes it difficult to locate the shared libraries later on.
        // Therefore, we also record the name with common shared library extensions added.
        if (!lowerCasedName.contains(".") && (lowerCasedName.startsWith("lib") || lowerCasedName.endsWith("lib") || lowerCasedName.contains("library"))) {
            SystemEventMonitor.record(SystemInstrumentationEvent.Action.OPEN, SystemInstrumentationEvent.Target.RESOURCE, (printedName + ".dll"));
            SystemEventMonitor.record(SystemInstrumentationEvent.Action.OPEN, SystemInstrumentationEvent.Target.RESOURCE, (printedName + ".so"));
        }
        SystemEventMonitor.record(SystemInstrumentationEvent.Action.OPEN, SystemInstrumentationEvent.Target.RESOURCE, printedName);
    }
}
