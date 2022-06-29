package edu.tum.sse.jtec.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OSUtilsTest {

    @Test
    void shouldWrapEnvironmentVariableInString() {
        // given
        String command = OSUtils.isWindows() ? "cmd /c JTEC_PID %JTEC_PID%" : "bash -c JTEC_PID $JTEC_PID ${JTEC_PID}";
        String envVar = "JTEC_PID";
        String expected = OSUtils.isWindows() ? "cmd /c %JTEC_PID% %JTEC_PID%" : "bash -c ${JTEC_PID} $JTEC_PID ${JTEC_PID}";

        // when
        String actual = OSUtils.wrapEnvironmentVariable(command, envVar);

        // then
        assertEquals(expected, actual);
    }
}
