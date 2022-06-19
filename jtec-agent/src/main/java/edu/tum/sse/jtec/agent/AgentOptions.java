package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instrumentation.coverage.CoverageLevel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgentOptions {
    // Output.
    public static final String AGENT_OUTPUT = "jtec.out";
    public static final String DEFAULT_AGENT_OUTPUT = "";

    // Test event instrumentation.
    public static final String TRACE_TEST_EVENTS = "test.trace";

    // System event instrumentation.
    public static final String TRACE_SYS_EVENTS = "sys.trace";

    // Coverage instrumentation.
    public static final String TRACE_COVERAGE = "cov.trace";
    public static final String COVERAGE_INSTRUMENT = "cov.instr";
    public static final String COVERAGE_LEVEL = "cov.level";
    public static final String COVERAGE_INCLUDES = "cov.includes";
    public static final String COVERAGE_EXCLUDES = "cov.excludes";
    public static final CoverageLevel DEFAULT_COVERAGE_LEVEL = CoverageLevel.CLASS;
    public static final String DEFAULT_COVERAGE_INCLUDES = ".*";
    public static final String DEFAULT_COVERAGE_EXCLUDES = "(sun|java|com.sun|edu.tum.sse.jtec|net.bytebuddy|org.apache.maven|org.junit).*";

    // Pre-test hook.
    public static final String PRE_TEST_COMMAND = "init.cmd";
    public static final String DEFAULT_PRE_TEST_COMMAND = "";

    public static final AgentOptions DEFAULT_OPTIONS = new AgentOptions(
            false,
            false,
            false,
            false,
            DEFAULT_COVERAGE_LEVEL,
            DEFAULT_COVERAGE_INCLUDES,
            DEFAULT_COVERAGE_EXCLUDES,
            DEFAULT_PRE_TEST_COMMAND,
            Paths.get(DEFAULT_AGENT_OUTPUT).toAbsolutePath()
    );

    private static final String OPTIONS_SEPARATOR = ",";
    private static final String VALUE_SEPARATOR = "=";

    private boolean traceSystemEvents = false;
    private boolean traceTestEvents = false;
    private boolean traceCoverage = false;
    private boolean instrumentCoverage = false;
    private CoverageLevel coverageLevel;
    private String coverageIncludes;
    private String coverageExcludes;
    private String preTestCommand;
    private Path outputPath;

    private AgentOptions() {
    }

    private AgentOptions(
            final boolean traceTestEvents,
            final boolean traceSystemEvents,
            final boolean traceCoverage,
            final boolean instrumentCoverage,
            final CoverageLevel coverageLevel,
            final String coverageIncludes,
            final String coverageExcludes,
            final String preTestCommand,
            final Path outputPath
    ) {
        this.traceTestEvents = traceTestEvents;
        this.traceSystemEvents = traceSystemEvents;
        this.traceCoverage = traceCoverage;
        this.instrumentCoverage = instrumentCoverage;
        this.coverageLevel = coverageLevel;
        this.coverageIncludes = coverageIncludes;
        this.coverageExcludes = coverageExcludes;
        this.preTestCommand = preTestCommand;
        this.outputPath = outputPath;
    }

    public static AgentOptions fromString(final String options) {
        final AgentOptions result = new AgentOptions();
        final Map<String, String> optionsInput = extractOptions(options);
        parseOutputDirectory(result, optionsInput);
        parseTestEventParams(result, optionsInput);
        parseSysEventParams(result, optionsInput);
        parseCoverageParams(result, optionsInput);
        parsePreTestParams(result, optionsInput);
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

    private static void parseOutputDirectory(final AgentOptions result, final Map<String, String> optionsInput) {
        result.outputPath = Paths.get(optionsInput.getOrDefault(AGENT_OUTPUT, DEFAULT_AGENT_OUTPUT)).toAbsolutePath();
    }

    private static void parseTestEventParams(final AgentOptions result, final Map<String, String> optionsInput) {
        result.traceTestEvents = Boolean.parseBoolean(optionsInput.get(TRACE_TEST_EVENTS));
    }

    private static void parseSysEventParams(final AgentOptions result, final Map<String, String> optionsInput) {
        result.traceSystemEvents = Boolean.parseBoolean(optionsInput.get(TRACE_SYS_EVENTS));
    }

    private static void parseCoverageParams(final AgentOptions result, final Map<String, String> optionsInput) {
        result.traceCoverage = Boolean.parseBoolean(optionsInput.get(TRACE_COVERAGE));
        result.coverageLevel = CoverageLevel.valueOf(optionsInput.getOrDefault(COVERAGE_LEVEL, DEFAULT_COVERAGE_LEVEL.toString()).toUpperCase());
        if (result.coverageLevel == CoverageLevel.METHOD) {
            result.instrumentCoverage = true;
        } else {
            result.instrumentCoverage = Boolean.parseBoolean(optionsInput.get(COVERAGE_INSTRUMENT));
        }
        result.coverageIncludes = optionsInput.getOrDefault(COVERAGE_INCLUDES, DEFAULT_COVERAGE_INCLUDES);
        result.coverageExcludes = optionsInput.getOrDefault(COVERAGE_EXCLUDES, DEFAULT_COVERAGE_EXCLUDES);
    }

    private static void parsePreTestParams(final AgentOptions result, final Map<String, String> optionsInput) {
        result.preTestCommand = optionsInput.getOrDefault(PRE_TEST_COMMAND, DEFAULT_PRE_TEST_COMMAND);
    }

    public String toAgentString() {
        return AGENT_OUTPUT + VALUE_SEPARATOR + outputPath +
                OPTIONS_SEPARATOR + TRACE_TEST_EVENTS + VALUE_SEPARATOR + traceTestEvents +
                OPTIONS_SEPARATOR + TRACE_SYS_EVENTS + VALUE_SEPARATOR + traceSystemEvents +
                OPTIONS_SEPARATOR + TRACE_COVERAGE + VALUE_SEPARATOR + traceCoverage +
                OPTIONS_SEPARATOR + COVERAGE_INSTRUMENT + VALUE_SEPARATOR + instrumentCoverage +
                OPTIONS_SEPARATOR + COVERAGE_LEVEL + VALUE_SEPARATOR + coverageLevel +
                OPTIONS_SEPARATOR + COVERAGE_INCLUDES + VALUE_SEPARATOR + coverageIncludes +
                OPTIONS_SEPARATOR + COVERAGE_EXCLUDES + VALUE_SEPARATOR + coverageExcludes +
                OPTIONS_SEPARATOR + PRE_TEST_COMMAND + VALUE_SEPARATOR + preTestCommand;
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

    public String getPreTestCommand() {
        return preTestCommand;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(final Path outputPath) {
        this.outputPath = outputPath;
    }
}
