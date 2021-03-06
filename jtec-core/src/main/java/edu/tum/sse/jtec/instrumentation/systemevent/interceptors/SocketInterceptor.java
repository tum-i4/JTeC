package edu.tum.sse.jtec.instrumentation.systemevent.interceptors;

import edu.tum.sse.jtec.instrumentation.systemevent.SystemEventMonitor;
import edu.tum.sse.jtec.instrumentation.systemevent.SystemInstrumentationEvent;
import net.bytebuddy.asm.Advice;

import java.net.SocketAddress;

/**
 * Interceptor that handles method calls with {@link SocketAddress} parameter.
 */
public class SocketInterceptor {

    /**
     * Writes the given socket address to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final SocketAddress address) {
        SystemEventMonitor.record(SystemInstrumentationEvent.Action.CONNECT, SystemInstrumentationEvent.Target.SOCKET, address.toString());
    }
}
