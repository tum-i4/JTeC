package edu.tum.sse.jtec.agent;

import java.lang.instrument.Instrumentation;

public class JTeCAgent {
    public static void premain(String arguments, Instrumentation instrumentation) {
        if (arguments == null) {
            throw new RuntimeException("Missing arguments with log file path for instrumentation.");
        }
        AgentOptions options = AgentOptions.fromString(arguments);
        System.err.println("Attaching JTeC agent with args: " + options.toAgentString());

        new Tracer(instrumentation, options);
    }
}
