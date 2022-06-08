# Sample Maven Project

## Execute Tests with Tracing and Test Listener enabled

```shell
mvn clean verify -fn -Djtec.opts=test.trace=true,test.out=testEvents.log,sys.trace=true,sys.out=sysEvents.log
``` 

