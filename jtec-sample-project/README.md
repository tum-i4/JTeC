# Sample Maven Project

This project serves as a demo and testing project for `JTeC` features.

## Execute Tests with Tracing and Test Listener enabled

```shell
mvn clean verify -fn -Djtec.opts="test.trace,sys.trace,cov.trace"
``` 

## Execute Tests with Pre-test Hook

We can simply use the `init.cmd` option to instrument all test JVM processes with a [frida](https://frida.re/) agent (
see [script](./scripts/frida-agent.py)):

```shell
# Unix-like
mvn clean verify -fn -Djtec.opts="init.cmd='python $(pwd)/scripts/frida-agent.py -p \$JTEC_PID -o target/jtec/\${JTEC_PID}_123_sys.log'" -DforkCount=1 -DreuseForks=false

# Windows
mvn verify -fn -Djtec.opts="init.cmd='python %cd%\scripts\frida-agent.py -p %JTEC_PID% -o target\jtec\%JTEC_PID%_123_sys.log -i .*jtec-sample.*\..*'" -DforkCount=1 -DreuseForks=false
``` 

