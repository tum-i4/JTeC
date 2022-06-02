# Sample Maven Project

## Execute Tests with Tracing and Test Listener enabled

```shell
mvn clean verify -fn -DTESTING_OUTPUT_FILE="$(pwd)/testing.log" -Dmaven.surefire.debug="-javaagent:$(pwd)/../jtec-agent/target/jtec-agent-0.1-SNAPSHOT.jar=traceTestEvents=true,traceSysEvents=true" -Dmaven.failsafe.debug="-javaagent:$(pwd)/../jtec-agent/target/jtec-agent-0.1-SNAPSHOT.jar=traceTestEvents=true,traceSysEvents=true"
``` 

