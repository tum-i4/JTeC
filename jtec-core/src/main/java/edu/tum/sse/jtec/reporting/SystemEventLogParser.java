package edu.tum.sse.jtec.reporting;

import com.google.gson.reflect.TypeToken;
import edu.tum.sse.jtec.instrumentation.systemevent.SystemInstrumentationEvent;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;

import static edu.tum.sse.jtec.util.JSONUtils.fromJson;

public final class SystemEventLogParser {
    public SystemEventLogParser() {
    }

    private Optional<TestSuite> findMatchingTestSuite(SystemInstrumentationEvent event, List<TestSuite> testSuites) {
        if (testSuites == null || event == null) return Optional.empty();
        return testSuites.stream()
                .filter(testSuite -> testSuite.getStartTimestamp() <= event.getTimestamp() && testSuite.getEndTimestamp() >= event.getTimestamp())
                .findFirst();
    }

    public void parse(Path sysLog, Map<String, List<TestSuite>> testSuiteMap) throws IOException {
        // If a test spawns a subprocess and the subprocess is also instrumented,
        // we might end up with some PIDs that are both, the PID of a test suite and the PID of a subprocess
        // on systems that recycle PIDs (e.g., Windows).
        // This map tracks PIDs from spawned subprocesses and keeps a reference to the corresponding test suite
        // (this will also support multiple levels of child processes).
        Map<String, List<TestSuite>> spawnedProcessTestSuiteMap = new HashMap<>();

        Type type = new TypeToken<List<SystemInstrumentationEvent>>() {}.getType();
        List<SystemInstrumentationEvent> events = fromJson(sysLog, type);
        for (SystemInstrumentationEvent event : events) {
            switch (event.getAction()) {
                case OPEN:
                    if (event.getTarget() == SystemInstrumentationEvent.Target.FILE || event.getTarget() == SystemInstrumentationEvent.Target.RESOURCE) {
                        TestSuite testSuite = findMatchingTestSuite(event, testSuiteMap.get(event.getPid()))
                                .orElseGet(() -> findMatchingTestSuite(event, spawnedProcessTestSuiteMap.getOrDefault(event.getPid(), Collections.emptyList())).orElse(null));
                        if (testSuite != null) testSuite.getOpenedFiles().add(event.getValue());
                    }
                    break;
                case CONNECT:
                    if (event.getTarget() == SystemInstrumentationEvent.Target.SOCKET) {
                        TestSuite testSuite = findMatchingTestSuite(event, testSuiteMap.get(event.getPid()))
                                .orElseGet(() -> findMatchingTestSuite(event, spawnedProcessTestSuiteMap.getOrDefault(event.getPid(), Collections.emptyList())).orElse(null));
                        if (testSuite != null) testSuite.getConnectedSockets().add(event.getValue());
                    }
                    break;
                case START:
                    if (event.getTarget() == SystemInstrumentationEvent.Target.THREAD) {
                        TestSuite testSuite = findMatchingTestSuite(event, testSuiteMap.get(event.getPid()))
                                .orElseGet(() -> findMatchingTestSuite(event, spawnedProcessTestSuiteMap.getOrDefault(event.getPid(), Collections.emptyList())).orElse(null));
                        if (testSuite != null) testSuite.getStartedThreads().add(event.getValue());
                    }
                    break;
                case SPAWN:
                    if (event.getTarget() == SystemInstrumentationEvent.Target.PROCESS) {
                        TestSuite testSuite = findMatchingTestSuite(event, testSuiteMap.get(event.getPid()))
                                .orElseGet(() -> findMatchingTestSuite(event, spawnedProcessTestSuiteMap.getOrDefault(event.getPid(), Collections.emptyList())).orElse(null));
                        if (testSuite != null) {
                            String targetPid = event.getValue();
                            testSuite.getSpawnedProcesses().add(targetPid);
                            spawnedProcessTestSuiteMap.putIfAbsent(targetPid, new ArrayList<>());
                            spawnedProcessTestSuiteMap.get(targetPid).add(testSuite);
                        }
                    }
                    break;
            }
        }
    }
}
