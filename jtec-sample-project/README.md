# Sample Maven Project

## Execute Tests with Tracing and Test Listener enabled

```shell
mvn clean verify -fn -Djtec.opts=traceTestEvents=true,testEventOut=testEvents.log,traceSysEvents=true,sysEventOut=sysEvents.log
``` 

