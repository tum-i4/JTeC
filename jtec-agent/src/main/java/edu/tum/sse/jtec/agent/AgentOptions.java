package edu.tum.sse.jtec.agent;

import edu.tum.sse.jtec.instrumentation.coverage.CoverageLevel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AgentOptions {
    // Address issue on Win32 where passing a pipe in a regex, e.g., (x|y), breaks the agent.
    public static final String PIPE_REPLACEMENT = ";";

    // Output.
    public static final String AGENT_OUTPUT = "jtec.out";
    public static final String DEFAULT_AGENT_OUTPUT = "";

    // Test event instrumentation.
    public static final String TRACE_TEST_EVENTS = "test.trace";
    public static final String TEST_INSTRUMENT = "test.instr";

    // System event instrumentation.
    public static final String TRACE_SYS_EVENTS = "sys.trace";
    public static final String TRACE_SYS_FILE = "sys.file";
    public static final String TRACE_SYS_SOCKET = "sys.socket";
    public static final String TRACE_SYS_THREAD = "sys.thread";
    public static final String TRACE_SYS_PROCESS = "sys.process";
    public static final String FILE_INCLUDES = "sys.includes";
    public static final String FILE_EXCLUDES = "sys.excludes";
    public static final String DEFAULT_FILE_INCLUDES = ".*";
    public static final String DEFAULT_FILE_EXCLUDES = ".*.(log|tmp)";

    // Coverage instrumentation.
    public static final String TRACE_COVERAGE = "cov.trace";
    public static final String COVERAGE_INSTRUMENT = "cov.instr";
    public static final String COVERAGE_LEVEL = "cov.level";
    public static final String COVERAGE_INCLUDES = "cov.includes";
    public static final String COVERAGE_EXCLUDES = "cov.excludes";
    public static final CoverageLevel DEFAULT_COVERAGE_LEVEL = CoverageLevel.CLASS;
    public static final String DEFAULT_COVERAGE_INCLUDES = ".*";
    public static final String DEFAULT_COVERAGE_EXCLUDES = "(sun|java|jdk|com.sun|edu.tum.sse.jtec|net.bytebuddy|org.apache.maven|org.junit).*";

    // Pre-test hook.
    public static final String PRE_TEST_COMMAND = "init.cmd";
    public static final String DEFAULT_PRE_TEST_COMMAND = "";

    public static final AgentOptions DEFAULT_OPTIONS = new AgentOptions(
            false,
            true,
            false,
            true,
            true,
            true,
            true,
            false,
            false,
            DEFAULT_COVERAGE_LEVEL,
            DEFAULT_COVERAGE_INCLUDES,
            DEFAULT_COVERAGE_EXCLUDES,
            DEFAULT_PRE_TEST_COMMAND,
            Paths.get(DEFAULT_AGENT_OUTPUT).toAbsolutePath(),
            DEFAULT_FILE_INCLUDES,
            DEFAULT_FILE_EXCLUDES
    );

    private static final String OPTIONS_SEPARATOR = ",";
    private static final String VALUE_SEPARATOR = "=";

    private boolean traceSystemEvents = false;
    private boolean traceTestEvents = false;
    private boolean instrumentFileEvents;
    private boolean instrumentSocketEvents;
    private boolean instrumentThreadEvents;
    private boolean instrumentProcessEvents;
    private boolean traceCoverage = false;
    private boolean instrumentCoverage = false;
    private boolean instrumentTestEvents = true;
    private CoverageLevel coverageLevel;
    private String coverageIncludes;
    private String coverageExcludes;
    private String preTestCommand;
    private Path outputPath;

    public void setFileIncludes(String fileIncludes) {
        this.fileIncludes = fileIncludes;
    }

    private String fileIncludes;
    private String fileExcludes;

    private AgentOptions() {
    }

    private AgentOptions(
            final boolean traceTestEvents,
            final boolean instrumentTestEvents,
            final boolean traceSystemEvents,
            final boolean instrumentFileEvents,
            final boolean instrumentSocketEvents,
            final boolean instrumentThreadEvents,
            final boolean instrumentProcessEvents,
            final boolean traceCoverage,
            final boolean instrumentCoverage,
            final CoverageLevel coverageLevel,
            final String coverageIncludes,
            final String coverageExcludes,
            final String preTestCommand,
            final Path outputPath,
            final String fileIncludes,
            final String fileExcludes
    ) {
        this.traceTestEvents = traceTestEvents;
        this.instrumentTestEvents = instrumentTestEvents;
        this.traceSystemEvents = traceSystemEvents;
        this.instrumentFileEvents = instrumentFileEvents;
        this.instrumentSocketEvents = instrumentSocketEvents;
        this.instrumentThreadEvents = instrumentThreadEvents;
        this.instrumentProcessEvents = instrumentProcessEvents;
        this.traceCoverage = traceCoverage;
        this.instrumentCoverage = instrumentCoverage;
        this.coverageLevel = coverageLevel;
        this.coverageIncludes = coverageIncludes;
        this.coverageExcludes = coverageExcludes;
        this.preTestCommand = preTestCommand;
        this.outputPath = outputPath;
        this.fileIncludes = fileIncludes;
        this.fileExcludes = fileExcludes;
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
                                        part.substring(0, eqIndex), part.substring(eqIndex + 1).replaceAll("^\"|\"$", "").replaceAll("^'|'$", ""));
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
        result.instrumentTestEvents = Boolean.parseBoolean(optionsInput.getOrDefault(TEST_INSTRUMENT, "true"));
    }

    private static void parseSysEventParams(final AgentOptions result, final Map<String, String> optionsInput) {
        result.traceSystemEvents = Boolean.parseBoolean(optionsInput.get(TRACE_SYS_EVENTS));
        result.instrumentFileEvents = Boolean.parseBoolean(optionsInput.getOrDefault(TRACE_SYS_FILE, "true"));
        result.instrumentSocketEvents = Boolean.parseBoolean(optionsInput.getOrDefault(TRACE_SYS_SOCKET, "true"));
        result.instrumentThreadEvents = Boolean.parseBoolean(optionsInput.getOrDefault(TRACE_SYS_THREAD, "true"));
        result.instrumentProcessEvents = Boolean.parseBoolean(optionsInput.getOrDefault(TRACE_SYS_PROCESS, "true"));
        result.fileIncludes = optionsInput.getOrDefault(FILE_INCLUDES, DEFAULT_FILE_INCLUDES).replace(PIPE_REPLACEMENT, "|");
        result.fileExcludes = optionsInput.getOrDefault(FILE_EXCLUDES, DEFAULT_FILE_EXCLUDES).replace(PIPE_REPLACEMENT, "|");
    }

    private static void parseCoverageParams(final AgentOptions result, final Map<String, String> optionsInput) {
        result.traceCoverage = Boolean.parseBoolean(optionsInput.get(TRACE_COVERAGE));
        result.coverageLevel = CoverageLevel.valueOf(optionsInput.getOrDefault(COVERAGE_LEVEL, DEFAULT_COVERAGE_LEVEL.toString()).toUpperCase());
        if (result.coverageLevel == CoverageLevel.METHOD) {
            result.instrumentCoverage = true;
        } else {
            result.instrumentCoverage = Boolean.parseBoolean(optionsInput.get(COVERAGE_INSTRUMENT));
        }
        result.coverageIncludes = optionsInput.getOrDefault(COVERAGE_INCLUDES, DEFAULT_COVERAGE_INCLUDES).replace(PIPE_REPLACEMENT, "|");
        result.coverageExcludes = optionsInput.getOrDefault(COVERAGE_EXCLUDES, DEFAULT_COVERAGE_EXCLUDES).replace(PIPE_REPLACEMENT, "|");
    }

    private static void parsePreTestParams(final AgentOptions result, final Map<String, String> optionsInput) {
        result.preTestCommand = optionsInput.getOrDefault(PRE_TEST_COMMAND, DEFAULT_PRE_TEST_COMMAND);
    }

    public String toAgentString() {
        return AGENT_OUTPUT + VALUE_SEPARATOR + outputPath +
                OPTIONS_SEPARATOR + TRACE_TEST_EVENTS + VALUE_SEPARATOR + traceTestEvents +
                OPTIONS_SEPARATOR + TEST_INSTRUMENT + VALUE_SEPARATOR + instrumentTestEvents +
                OPTIONS_SEPARATOR + TRACE_SYS_EVENTS + VALUE_SEPARATOR + traceSystemEvents +
                OPTIONS_SEPARATOR + TRACE_SYS_FILE + VALUE_SEPARATOR + instrumentFileEvents +
                OPTIONS_SEPARATOR + TRACE_SYS_SOCKET + VALUE_SEPARATOR + instrumentSocketEvents +
                OPTIONS_SEPARATOR + TRACE_SYS_THREAD + VALUE_SEPARATOR + instrumentThreadEvents +
                OPTIONS_SEPARATOR + TRACE_SYS_PROCESS + VALUE_SEPARATOR + instrumentProcessEvents +
                OPTIONS_SEPARATOR + FILE_INCLUDES + VALUE_SEPARATOR + "\"" + fileIncludes + "\"" +
                OPTIONS_SEPARATOR + FILE_EXCLUDES + VALUE_SEPARATOR + "\"" + fileExcludes + "\"" +
                OPTIONS_SEPARATOR + TRACE_COVERAGE + VALUE_SEPARATOR + traceCoverage +
                OPTIONS_SEPARATOR + COVERAGE_INSTRUMENT + VALUE_SEPARATOR + instrumentCoverage +
                OPTIONS_SEPARATOR + COVERAGE_LEVEL + VALUE_SEPARATOR + coverageLevel +
                OPTIONS_SEPARATOR + COVERAGE_INCLUDES + VALUE_SEPARATOR + "\"" + coverageIncludes + "\"" +
                OPTIONS_SEPARATOR + COVERAGE_EXCLUDES + VALUE_SEPARATOR + "\"" + coverageExcludes + "\"" +
                OPTIONS_SEPARATOR + PRE_TEST_COMMAND + VALUE_SEPARATOR + "\"" + preTestCommand + "\"";
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

    public String getFileIncludes() {
        return fileIncludes;
    }

    public String getFileExcludes() {
        return fileExcludes;
    }

    public boolean shouldInstrumentTestEvents() {
        return instrumentTestEvents;
    }

    public boolean shouldInstrumentFileEvents() {
        return instrumentFileEvents;
    }

    public boolean shouldInstrumentSocketEvents() {
        return instrumentSocketEvents;
    }

    public boolean shouldInstrumentThreadEvents() {
        return instrumentThreadEvents;
    }

    public boolean shouldInstrumentProcessEvents() {
        return instrumentProcessEvents;
    }
}
