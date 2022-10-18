# JTeC Agent

The JTeC agent performs dynamic runtime instrumentation through Java bytecode transformation.
It currently supports instrumenting test events (e.g., test started/ended), system events (e.g., file open, socket
connect), and coverage (e.g., class-level coverage).

## Options

To set up the agent that is attached to a JVM via `-javaagent:/path/to/agent.jar`, several runtime options can be
provided as key value pairs `-javaagent:/path/to/agent.jar=key1=val1,key2=val2,...`:

| Key          | Type    | Default value                                                                   | Description                                                                                  |
|--------------|---------|---------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| jtec.out     | String  | `.`                                                                             | Output path for jtec output                                                                  |
| jtec.optsfile| String  | `jtec.txt`                                                                      | Options file containing JTeC options                                                         |
| test.trace   | Boolean | `false`                                                                         | Enables test event tracing                                                                   |
| test.instr   | Boolean | `true`                                                                          | Enables class file instrumentation for JUnit classes (instead of providing custom listeners) |
| sys.trace    | Boolean | `false`                                                                         | Enables system event tracing                                                                 |
| sys.file     | Boolean | `true`                                                                          | Enables file event tracing                                                                   |
| sys.socket   | Boolean | `true`                                                                          | Enables socket event tracing                                                                 |
| sys.thread   | Boolean | `true`                                                                          | Enables thread event tracing                                                                 |
| sys.process  | Boolean | `true`                                                                          | Enables process event tracing                                                                |
| sys.includes | String  | `.*`                                                                            | Regex for included files                                                                     |
| sys.excludes | String  | `.*.(log\|tmp)`                                                                 | Regex for excluded files                                                                     |
| cov.trace    | Boolean | `false`                                                                         | Enables coverage tracing                                                                     |
| cov.level    | String  | `class`                                                                         | Coverage level: `class` or `method`                                                          |
| cov.instr    | Boolean | `false` (`true` if `cov.level=method`)                                          | Enables class file instrumentation (only needed for `method` or non-forked `class` coverage) |
| cov.includes | String  | `.*`                                                                            | Regex for included Java classes                                                              |
| cov.excludes | String  | `(sun\|java\|jdk\|com.sun\|edu.tum.sse.jtec\|net.bytebuddy\|org.apache.maven).*`| Regex for excluded Java classes                                                              |
| init.cmd     | String  | `null`                                                                          | Command to execute upon JVM initialization (pre-test hook)                                   |

