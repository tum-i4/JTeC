package edu.tum.sse.jtec.testlistener;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AgentOptions {
    private final boolean traceTestEvents = false;
    private Path testEventOutputPath;

    public static AgentOptions fromString(final String options) {
        final AgentOptions result = new AgentOptions();

        final String[] optionParts = options.split(",");
        final Map<String, String> presentOptions = new HashMap<>();
        for (final String part : optionParts) {
            final String[] keyValue = part.trim().split("=");
            if (keyValue.length != 2) {
                throw new IllegalArgumentException(part + " is not in the right format of key=value");
            }
            presentOptions.put(keyValue[0], keyValue[1]);
        }

        return result;
    }

    public Path getTestEventOutputPath() {
        return testEventOutputPath;
    }

    public boolean getTraceTestEvents() {
        return traceTestEvents;
    }
}
