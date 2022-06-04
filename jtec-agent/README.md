# JTeC Agent

The JTeC agent performs dynamic runtime instrumentation through Java bytecode transformation.
It currently supports instrumenting test events (e.g., test started/ended), system events (e.g., file open, socket
connect), and coverage (e.g., class-level coverage).

## Options

To set up the agent that is attached to a JVM via `-javaagent:/path/to/agent.jar`, several runtime options can be
provided as key value pairs `-javaagent:/path/to/agent.jar=key1=val1,key2=val2,...`:

| Key             | Type    | Default value      | Description                                                                                  |
|-----------------|---------|--------------------|----------------------------------------------------------------------------------------------|         
| test.trace      | Boolean | `false`            | Enables test event tracing                                                                   |
| test.out        | String  | `./testEvents.log` | Output file for test events                                                                  |
| sys.trace       | Boolean | `false`            | Enables system event tracing                                                                 |
| sys.out         | String  | `./sysEvents.log`  | Output file for system events                                                                |
| cov.trace       | Boolean | `false`            | Enables coverage tracing                                                                     |
| cov.out         | String  | `./coverage.log`   | Output file for coverage dump                                                                |
| cov.instr       | Boolean | `false`            | Enables class file instrumentation (only needed for `method` or non-forked `class` coverage) |
| cov.level       | String  | `class`            | Coverage level: `class` or `method`                                                          |
| cov.includes    | String  | `.*`               | Regex for included Java classes                                                              |
| cov.excludes    | String  | `(sun\|java\|jdk).*` | Regex for excluded Java classes                                                              |
