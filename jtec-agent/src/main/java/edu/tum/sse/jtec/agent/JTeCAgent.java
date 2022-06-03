package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instr.AgentOptions;
import edu.tum.sse.jtec.instr.Tracer;

import java.lang.instrument.Instrumentation;

public class JTeCAgent {
    public static void premain(String arguments, Instrumentation instrumentation) {
        if (arguments == null) {
            throw new RuntimeException("Missing arguments with log file path for instrumentation.");
        }
        System.out.println("Attach JTeC agent with args: " + arguments);
        AgentOptions options = AgentOptions.fromString(arguments);

        new Tracer(instrumentation, options);
    }
}
