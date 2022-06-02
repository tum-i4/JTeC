package edu.tum.sse.jtec.testlistener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class AgentOptions {

    public final static AgentOptions DEFAULT_OPTIONS = new AgentOptions(
            false,
            Paths.get("").toAbsolutePath()
    );
    private boolean traceTestEvents = false;
    private Path testEventOutputPath;

    private AgentOptions() {
    }

    private AgentOptions(boolean traceTestEvents, Path testEventOutputPath) {
        this.traceTestEvents = traceTestEvents;
        this.testEventOutputPath = testEventOutputPath;
    }

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

        if (presentOptions.containsKey("traceTestEvents")) {
            result.traceTestEvents = Boolean.parseBoolean(presentOptions.get("traceTestEvents"));
            if (presentOptions.containsKey("testEventOut")) {
                result.testEventOutputPath = Paths.get(presentOptions.get("testEventOut"));
                if (!result.testEventOutputPath.toFile().exists()) {
                    try {
                        Files.createFile(result.testEventOutputPath);
                    } catch (final IOException exception) {
                        System.err.println("Failed to open or create output file.");
                        exception.printStackTrace();
                    }
                }
            }
        }

        return result;
    }

    public String toAgentString() {
        return "traceTestEvents=" + traceTestEvents +
                ",testEventOutputPath=" + testEventOutputPath;
    }

    public Path getTestEventOutputPath() {
        return testEventOutputPath;
    }

    public boolean getTraceTestEvents() {
        return traceTestEvents;
    }
}
