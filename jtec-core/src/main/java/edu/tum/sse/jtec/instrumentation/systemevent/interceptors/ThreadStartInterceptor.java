package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.SystemEventMonitor;
import edu.tum.sse.jtec.instrumentation.systemevent.SystemInstrumentationEvent;
import net.bytebuddy.asm.Advice;

public class ThreadStartInterceptor {
    /**
     * Writes the given path to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodEnter
    public static void enter(@Advice.FieldValue(value = "name") final String threadName) {
        SystemEventMonitor.record(SystemInstrumentationEvent.Action.START, SystemInstrumentationEvent.Target.THREAD, threadName);
    }
}
