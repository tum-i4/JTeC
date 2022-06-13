package edu.tum.sse.jtec.util;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ProcessUtilsTest {

    @Test
    void shouldRunCommandInProcess() throws IOException, InterruptedException {
        // given
        String command = OSUtils.isWindows() ? "dir" : "ls";
        boolean shouldBlock = true;

        // when
        Process process = ProcessUtils.run(command, shouldBlock);
        String output = new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.joining("\n"));

        // then
        assertFalse(process.isAlive());
        assertEquals(0, process.exitValue());
        assertFalse(output.isEmpty());
    }
}
