package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.SystemEventMonitor;
import edu.tum.sse.jtec.instrumentation.systemevent.SystemInstrumentationEvent;
import net.bytebuddy.asm.Advice;

/**
 * Interceptor that handles method calls with {@link String} parameter.
 */
public class StringPathInterceptor {

    /**
     * Writes the given path to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final String printedName) {
        SystemEventMonitor.record(SystemInstrumentationEvent.Action.OPEN, SystemInstrumentationEvent.Target.FILE, printedName);
    }
}
