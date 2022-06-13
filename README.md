# JTeC: Java Testwise Coverage

JTeC is a tool that enables researchers to obtain testwise coverage for programs that run in the JVM.
It targets research in regression testing, such as test selection, prioritization, or flaky test analysis.
To analyze tests beyond code coverage, JTeC also supports instrumenting APIs that open external files (e.g., `java.io`
and `java.nio`),
connect to sockets (e.g., `java.net.socket`), or spawn threads or processes (e.g., `java.lang`).

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
            <version>0.0.1-SNAPSHOT</version>
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

## Structure

```
├── jtec-agent              <- The JTeC agent is a simple Java agent that can be parameterized and attached to a JVM process.
├── jtec-core               <- The JTeC core package contains instrumentation and auxiliary code.
├── jtec-maven-plugin       <- The JTeC maven plugin provides a utility to attach the agent to the tests of a Maven project.
└── jtec-sample-project     <- A sample project that demonstrates how to use JTeC in a Maven multi-module project.
```

## Setup

To build JTeC simply run:

```shell
$ mvn clean install 
```

This will build the code for all JTeC projects, run all tests, and install the JARs to your local Maven repository.
