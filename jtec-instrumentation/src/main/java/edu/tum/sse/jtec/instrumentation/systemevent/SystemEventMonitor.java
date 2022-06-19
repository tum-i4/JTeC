package edu.tum.sse.jtec.instrumentation.systemevent;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SystemEventMonitor {

    private static final Collection<SystemInstrumentationEvent> events = new ConcurrentLinkedQueue<>();
    private static final String pid = java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

    public static Collection<SystemInstrumentationEvent> getEvents() {
        return events;
    }

    public static void record(final SystemInstrumentationEvent.Action action, final SystemInstrumentationEvent.Target target, final String value) {
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
