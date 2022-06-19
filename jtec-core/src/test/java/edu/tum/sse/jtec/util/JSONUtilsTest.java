package edu.tum.sse.jtec.util;

import edu.tum.sse.jtec.instrumentation.systemevent.SystemInstrumentationEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edu.tum.sse.jtec.util.IOUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class JSONUtilsTest {

    @Test
    void shouldSerializeEvent() {
        // given
        long timestamp = System.currentTimeMillis();
        String pid = "123";
        String value = "C:\\path\\to\\file.cpp";
        SystemInstrumentationEvent event = new SystemInstrumentationEvent(
                timestamp,
                pid,
                SystemInstrumentationEvent.Action.OPEN,
                SystemInstrumentationEvent.Target.FILE,
                value
        );

        // when
        String json = JSONUtils.toJson(event);

        // then
        assertEquals(String.format("{\"timestamp\":%d,\"pid\":\"123\",\"action\":\"OPEN\",\"target\":\"FILE\",\"value\":\"C:\\\\path\\\\to\\\\file.cpp\"}", timestamp), json);
    }

    @Test
    void shouldSerializeListOfEvents() {
        // given
        long timestamp = System.currentTimeMillis();
        String pid = "123";
        String value = "C:\\path\\to\\file.cpp";
        SystemInstrumentationEvent event = new SystemInstrumentationEvent(
                timestamp,
                pid,
                SystemInstrumentationEvent.Action.OPEN,
                SystemInstrumentationEvent.Target.FILE,
                value
        );
        List<SystemInstrumentationEvent> events = new ArrayList<>();
        events.add(event);

        // when
        String json = JSONUtils.toJson(events);

        // then
        assertEquals(String.format("[{\"timestamp\":%d,\"pid\":\"123\",\"action\":\"OPEN\",\"target\":\"FILE\",\"value\":\"C:\\\\path\\\\to\\\\file.cpp\"}]", timestamp), json);
    }

}
