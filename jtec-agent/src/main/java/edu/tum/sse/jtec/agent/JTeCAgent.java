package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.testlistener.AgentOptions;
import edu.tum.sse.jtec.testlistener.Tracer;

import java.lang.instrument.Instrumentation;

public class JTeCAgent {
    public static void premain(String arguments, Instrumentation instrumentation) {
        if (arguments == null) {
            throw new RuntimeException("Missing arguments with log file path for instrumentation.");
        }
        // TODO maybe we should add an actual logger, though it may not be initialized at this point.
        System.err.println("Attaching filetracer agent with args: " + arguments);
        AgentOptions options = AgentOptions.fromString(arguments);

        new Tracer(instrumentation, options);
    }
}
