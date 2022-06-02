package edu.tum.sse.jtec.testlistener.syseventinstrumentation.interceptors;

import edu.tum.sse.jtec.testlistener.syseventinstrumentation.AdviceOutput;
import edu.tum.sse.jtec.testlistener.syseventinstrumentation.AdvicePid;
import net.bytebuddy.asm.Advice;

import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Interceptor that handles method calls with {@link SocketAddress} parameter.
 */
public class SocketInterceptor {

    /**
     * Writes the given socket address to the location given in the {@code outputPath} parameter.
     */
    @Advice.OnMethodEnter
    public static void enter(@Advice.Argument(0) final SocketAddress address, @AdviceOutput final String outputPath, @AdvicePid final String currentPid) {
        final Long timestamp = System.currentTimeMillis() * 1000000;
        try {
            final String message = String.format("{\"timestamp\": %d, \"pid\": \"%s\", \"action\": \"CONNECT\", \"target\": \"SOCKET\", \"value\": \"%s\"}\n",
                    timestamp, currentPid, address);
            Files.write(Paths.get(outputPath), message.getBytes(), StandardOpenOption.APPEND,
                    StandardOpenOption.CREATE);
        } catch (final Exception e) {
            System.err.println("Exception, address is: " + address);
            e.printStackTrace();
        }
    }
}
