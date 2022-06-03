# JTeC Agent

The JTeC agent performs dynamic runtime instrumentation through Java bytecode transformation.
It currently supports instrumenting test events (e.g., test started/ended), system events (e.g., file open, socket
connect), and coverage (e.g., class-level coverage).

## Options

To set up the agent that is attached to a JVM via `-javaagent:/path/to/agent.jar`, several runtime options can be
provided as key value pairs `-javaagent:/path/to/agent.jar:key1=val1,key2=val2,...`:

| Key             | Type    | Default value    |
|-----------------|---------|------------------|
| traceTestEvents | Boolean | false            |
| testEventOut    | String  | ./testEvents.log |
| traceSysEvents  | Boolean | false            |
| sysEventOut     | String  | ./sysEvents.log  |
