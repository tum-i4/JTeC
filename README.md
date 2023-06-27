# JTeC: Java Testwise Coverage

![CI](https://github.com/tum-i4/JTeC/actions/workflows/maven.yml/badge.svg)

JTeC is a tool that enables researchers to obtain testwise coverage for programs that run in the JVM.
It targets research in regression testing, such as test selection, prioritization, or flaky test analysis.
To analyze tests beyond code coverage, JTeC also supports instrumenting APIs that open external files (e.g., `java.io`
and `java.nio`),
connect to sockets (e.g., `java.net.socket`), or spawn threads or processes (e.g., `java.lang`).
Notably, JTeC thus doesn't rely on the Java [`SecurityManager`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/SecurityManager.html), which is [outdated and will be removed in newer JDKs](https://bugs.openjdk.org/browse/JDK-8264713).

## Acknowledgments & Contributors

The project is developed as part of the BMBF funded project Q-Soft by [CQSE](https://www.cqse.eu/en/)
and [TUM](https://www.in.tum.de/i04/).
JTeC is mainly developed by Raphael Noemmer and Daniel Elsner.

## Usage

JTeC is built to be used with Maven Surefire and Failsafe.
Therefore, the simplest way to use JTeC in a Maven project is through the JTeC Maven plugin:

```xml

<build>
    <plugins>
        <plugin>
            <groupId>edu.tum.sse</groupId>
            <artifactId>jtec-maven-plugin</artifactId>
            <version>0.0.5-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>jtec
                        </goal>  <!-- Instrument all JVMs spawned by Maven Surefire/Failsafe that execute tests -->
                        <goal>report
                        </goal>  <!-- Create reports for each Maven project the reactor after test execution -->
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Then, execute your tests as you would normally do, by optionally providing arguments to `JTeC`:

```shell
$ mvn clean verify -fn -Djtec.opts="test.trace,sys.trace,cov.trace"
```

After the command has finished, you'll find a JSON test report in each `target/jtec` directory, as well as the raw logs
created by JTeC.
If you want to have a single aggregated test report, e.g., in a Maven multi-module project, simply add
the `jtec:report-aggregate` goal to your command line:

```shell
$ mvn clean verify -fn -Djtec.opts="test.trace,sys.trace,cov.trace" jtec:report-aggregate
```

## Advanced JUnit Usage

If you do not want to use JTeC's default instrumentation for JUnit (4/5) test cases, you may pass the
option `test.instr=false`, which prevents bytecode transformation for JUnit classes.
Instead, for JUnit5 projects,
the [service loader mechanism](https://junit.org/junit5/docs/current/user-guide/#launcher-api-listeners-custom) is used
to dynamically register
a [`TestExecutionListener`](https://junit.org/junit5/docs/current/api/org.junit.platform.launcher/org/junit/platform/launcher/TestExecutionListener.html)
.
For JUnit4 projects, you can register a
JTeC [`RunListener`](https://junit.org/junit4/javadoc/4.12/org/junit/runner/notification/RunListener.html)
with [Maven](https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit.html#using-custom-listeners-and-reporters)
in your `pom.xml` as follows:

```xml

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId> <!-- same for failsafe -->
    <version>3.0.0-M5</version>
    <configuration>
        <properties>
            <property>
                <name>listener</name>
                <value>edu.tum.sse.jtec.instrumentation.testevent.JUnitTestEventListener</value>
            </property>
        </properties>
    </configuration>
</plugin>
```

## Structure

```
├── jtec-agent              <- The JTeC agent is a simple Java agent that can be parameterized and attached to a JVM process.
├── jtec-core               <- The JTeC core package contains instrumentation and auxiliary code.
├── jtec-instrumentation    <- The JTeC instrumentation package contains instrumentation code injected via the JTeC agent at runtime.
├── jtec-maven-plugin       <- The JTeC maven plugin provides a utility to attach the agent to the tests of a Maven project.
└── jtec-sample-project     <- A sample project that demonstrates how to use JTeC in a Maven multi-module project.
```

## Setup

To build JTeC simply run:

```shell
$ mvn clean install 
```

This will build the code for all JTeC projects, run all tests, and install the JARs to your local Maven repository.
