package edu.tum.sse.jtec.instrumentation.systemevent;

import java.util.ArrayList;
import java.util.List;

public class SysEventWriter {

    private static final List<SystemInstrumentationEvent> events = new ArrayList<>();
    private static final String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    public static List<SystemInstrumentationEvent> getEvents() {
        return events;
    }

    public static void writeMessage(final SystemInstrumentationEvent.Action action, final SystemInstrumentationEvent.Target target, final String value, final String outputPath) {
        SystemInstrumentationEvent event = new SystemInstrumentationEvent(
                System.currentTimeMillis(),
                pid,
                action,
                target,
                value
        );
        events.add(
                event
        );
    }

}
