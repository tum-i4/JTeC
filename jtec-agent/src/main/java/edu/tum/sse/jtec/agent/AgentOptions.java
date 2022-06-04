package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instrumentation.coverage.CoverageLevel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.tum.sse.jtec.util.IOUtils.createFileAndEnclosingDir;

public class AgentOptions {

    // Test event instrumentation.
    public static final String TRACE_TEST_EVENTS = "test.trace";
    public static final String TEST_EVENT_OUT = "test.out";
    public static final String DEFAULT_TEST_EVENT_OUT = "testEvents.log";

    // System event instrumentation.
    public static final String TRACE_SYS_EVENTS = "sys.trace";
    public static final String SYS_EVENT_OUT = "sys.out";
    public static final String DEFAULT_SYS_EVENT_OUT = "sysEvents.log";

    // Coverage instrumentation.
    public static final String TRACE_COVERAGE = "cov.trace";
    public static final String COVERAGE_OUT = "cov.out";
    public static final String COVERAGE_INSTRUMENT = "cov.instr";
    public static final String COVERAGE_LEVEL = "cov.level";
    public static final String COVERAGE_INCLUDES = "cov.includes";
    public static final String COVERAGE_EXCLUDES = "cov.excludes";
    public static final String DEFAULT_COVERAGE_OUT = "coverage.log";
    public static final CoverageLevel DEFAULT_COVERAGE_LEVEL = CoverageLevel.CLASS;
    public static final String DEFAULT_COVERAGE_INCLUDES = ".*";
    public static final String DEFAULT_COVERAGE_EXCLUDES = "(sun|java|jdk|com.sun|edu.tum.sse.jtec|net.bytebuddy|org.apache.maven).*";

    public static final AgentOptions DEFAULT_OPTIONS = new AgentOptions(
            false,
            Paths.get(DEFAULT_TEST_EVENT_OUT).toAbsolutePath(),
            false,
            Paths.get(DEFAULT_SYS_EVENT_OUT).toAbsolutePath(),
            false,
            Paths.get(DEFAULT_COVERAGE_OUT).toAbsolutePath(),
            false,
            DEFAULT_COVERAGE_LEVEL,
            DEFAULT_COVERAGE_INCLUDES,
            DEFAULT_COVERAGE_EXCLUDES
    );

    private static final String OPTIONS_SEPARATOR = ",";
    private static final String VALUE_SEPARATOR = "=";

    private boolean traceSystemEvents = false;
    private boolean traceTestEvents = false;
    private boolean traceCoverage = false;
    private boolean instrumentCoverage = false;
    private Path testEventOutputPath;
    private Path systemEventOutputPath;
    private Path coverageOutputPath;
    private CoverageLevel coverageLevel;
    private String coverageIncludes;
    private String coverageExcludes;

    private AgentOptions() {
    }

    private AgentOptions(
            final boolean traceTestEvents,
            final Path testEventOutputPath,
            final boolean traceSystemEvents,
            final Path systemEventOutputPath,
            final boolean traceCoverage,
            final Path coverageOutputPath,
            final boolean instrumentCoverage,
            final CoverageLevel coverageLevel,
            final String coverageIncludes,
            final String coverageExcludes
    ) {
        this.traceTestEvents = traceTestEvents;
        this.testEventOutputPath = testEventOutputPath;
        this.traceSystemEvents = traceSystemEvents;
        this.systemEventOutputPath = systemEventOutputPath;
        this.traceCoverage = traceCoverage;
        this.coverageOutputPath = coverageOutputPath;
        this.instrumentCoverage = instrumentCoverage;
        this.coverageLevel = coverageLevel;
        this.coverageIncludes = coverageIncludes;
        this.coverageExcludes = coverageExcludes;
    }

    public static AgentOptions fromString(final String options) {
        final AgentOptions result = new AgentOptions();
        final Map<String, String> optionsInput = extractOptions(options);
        parseTestEventParams(result, optionsInput);
        parseSysEventParams(result, optionsInput);
        parseCoverageParams(result, optionsInput);
        return result;
    }

    private static Map<String, String> extractOptions(String options) {
        if (options == null) {
            options = "";
        }
        return Stream.of(options.split(OPTIONS_SEPARATOR))
                .map(String::trim)
                .filter(part -> !part.isEmpty())
                .map(
                        part -> {
                            final int eqIndex = part.indexOf(VALUE_SEPARATOR);
                            if (eqIndex > 0) {
                                return new AbstractMap.SimpleEntry<>(
                                        part.substring(0, eqIndex), part.substring(eqIndex + 1));
                            }
                            // We also allow boolean flags, if only a key is provided.
                            return new AbstractMap.SimpleEntry<>(part, "true");
                        })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static void parseTestEventParams(final AgentOptions result, final Map<String, String> optionsInput) {
        if (!optionsInput.containsKey(TRACE_TEST_EVENTS)) {
            return;
        }
        result.traceTestEvents = Boolean.parseBoolean(optionsInput.get(TRACE_TEST_EVENTS));
        result.testEventOutputPath = Paths.get(optionsInput.getOrDefault(TEST_EVENT_OUT, DEFAULT_TEST_EVENT_OUT)).toAbsolutePath();
        createFileAndEnclosingDir(result.testEventOutputPath);
    }

    private static void parseSysEventParams(final AgentOptions result, final Map<String, String> optionsInput) {
        if (!optionsInput.containsKey(TRACE_SYS_EVENTS)) {
            return;
        }
        result.traceSystemEvents = Boolean.parseBoolean(optionsInput.get(TRACE_SYS_EVENTS));
        result.systemEventOutputPath = Paths.get(optionsInput.getOrDefault(SYS_EVENT_OUT, DEFAULT_SYS_EVENT_OUT)).toAbsolutePath();
        createFileAndEnclosingDir(result.systemEventOutputPath);
    }

    private static void parseCoverageParams(final AgentOptions result, final Map<String, String> optionsInput) {
        if (!optionsInput.containsKey(TRACE_COVERAGE)) {
            return;
        }
        result.traceCoverage = Boolean.parseBoolean(optionsInput.get(TRACE_COVERAGE));
        result.coverageOutputPath = Paths.get(optionsInput.getOrDefault(COVERAGE_OUT, DEFAULT_COVERAGE_OUT)).toAbsolutePath();
        createFileAndEnclosingDir(result.coverageOutputPath);
        result.coverageLevel = CoverageLevel.valueOf(optionsInput.getOrDefault(COVERAGE_LEVEL, DEFAULT_COVERAGE_LEVEL.toString()).toUpperCase());
        if (result.coverageLevel == CoverageLevel.METHOD) {
            result.instrumentCoverage = true;
        } else {
            result.instrumentCoverage = Boolean.parseBoolean(optionsInput.get(COVERAGE_INSTRUMENT));
        }
        result.coverageIncludes = optionsInput.getOrDefault(COVERAGE_INCLUDES, DEFAULT_COVERAGE_INCLUDES);
        result.coverageExcludes = optionsInput.getOrDefault(COVERAGE_EXCLUDES, DEFAULT_COVERAGE_EXCLUDES);
    }

    public String toAgentString() {
        return TRACE_TEST_EVENTS + VALUE_SEPARATOR + traceTestEvents +
                OPTIONS_SEPARATOR + TEST_EVENT_OUT + VALUE_SEPARATOR + testEventOutputPath +
                OPTIONS_SEPARATOR + TRACE_SYS_EVENTS + VALUE_SEPARATOR + traceSystemEvents +
                OPTIONS_SEPARATOR + SYS_EVENT_OUT + VALUE_SEPARATOR + systemEventOutputPath +
                OPTIONS_SEPARATOR + TRACE_COVERAGE + VALUE_SEPARATOR + traceCoverage +
                OPTIONS_SEPARATOR + COVERAGE_OUT + VALUE_SEPARATOR + coverageOutputPath +
                OPTIONS_SEPARATOR + COVERAGE_INSTRUMENT + VALUE_SEPARATOR + instrumentCoverage +
                OPTIONS_SEPARATOR + COVERAGE_LEVEL + VALUE_SEPARATOR + coverageLevel +
                OPTIONS_SEPARATOR + COVERAGE_INCLUDES + VALUE_SEPARATOR + coverageIncludes +
                OPTIONS_SEPARATOR + COVERAGE_EXCLUDES + VALUE_SEPARATOR + coverageExcludes;
    }

    public Path getTestEventOutputPath() {
        return testEventOutputPath;
    }

    public Path getSystemEventOutputPath() {
        return systemEventOutputPath;
    }

    public Path getCoverageOutputPath() {
        return coverageOutputPath;
    }

    public CoverageLevel getCoverageLevel() {
        return coverageLevel;
    }

    public String getCoverageIncludes() {
        return coverageIncludes;
    }

    public String getCoverageExcludes() {
        return coverageExcludes;
    }

    public boolean shouldTraceTestEvents() {
        return traceTestEvents;
    }

    public boolean shouldTraceSystemEvents() {
        return traceSystemEvents;
    }

    public boolean shouldTraceCoverage() {
        return traceCoverage;
    }

    public boolean shouldInstrumentCoverage() {
        return instrumentCoverage;
    }
}
