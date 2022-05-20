package edu.tum.sse.jtec.testlistener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AgentOptions {
    private Path outputPath;

    public static AgentOptions fromString(String options) {
        AgentOptions result = new AgentOptions();
        String[] optionParts = options.split(",");
        result.outputPath = Paths.get(optionParts[0]);
        if (!result.outputPath.toFile().exists()) {
            try {
                Files.createFile(result.outputPath);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to open or create output file.", exception);
            }
        }
        return result;
    }
}
