package edu.tum.sse.jtec.util;

import edu.tum.sse.jtec.instrumentation.systemevent.SystemInstrumentationEvent;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.jupiter.api.Assertions.*;

class JSONUtilsTest {

    @Test
    void testa() {
        ConcurrentMap<String, Set<String>> testMap = new ConcurrentHashMap<>();
        testMap.put("Test1", new HashSet<>(Arrays.asList("value1", "value2", "value3")));
        testMap.put("Test2", new HashSet<>(Arrays.asList("value1", "value2", "value3")));
        System.out.println(JSONUtils.toJson(testMap));
    }

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
