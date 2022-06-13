# JTeC Maven Plugin

This plugin simply attaches the JTeC agent JAR to a project that uses Maven Surefire and/or Maven Failsafe for (
integration) testing.

## Usage

Add plugin to Maven project:

```xml
<!-- Optionally put this inside a dedicated JTeC Maven profile. -->
<build>
    <plugins>
        <plugin>
            <groupId>edu.tum.sse</groupId>
            <artifactId>jtec-maven-plugin</artifactId>
            <version>0.0.2-SNAPSHOT</version>
            <executions>
                <execution>
                    <goals>
                        <goal>jtec</goal>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

This way, since the JTeC Mojo (`jtec` goal) is configured to execute before the `test` lifecycle phase, you're all set
for using JTeC.
The JTeC agent can be configured using `-Djtec.opts` as follows:

```shell
$ mvn test -Djtec.opts=traceTestEvents=true,...
```

However, there are already some reasonable defaults for the JTeC agent defined (e.g., store log files to the current
working directory).

## Report Generation

The goal `jtec:report` which by default runs as part of the `verify` lifecycle phase, generates JSON test reports in
the `target/jtec` directory of a Maven project.
If used on a multi-module project, each module has its own report.

## Aggregate Reports

The goal `jtec:report-aggregate` can be used to generate an aggregated test report in `target/jtec` of the root project
of a Maven multi-module project:

```shell
$ mvn clean verify -Djtec.opts="test.trace,sys.trace,cov.trace" jtec:report-aggregate
```
