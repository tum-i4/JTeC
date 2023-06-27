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
            <version>0.0.4</version>
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
$ mvn test -Djtec.opts="test.trace,sys.trace,cov.trace,..."
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

## Updating/Merging Reports

The goal `jtec:update-report` can be used to update an old test report with a new report by merging them:

```shell
$ mvn jtec:update-report -Djtec.oldReport="old-report.json" -Djtec.newReport="new-report.json" -Djtec.output="$(pwd)"
```

## Troubleshooting

In large projects, one might experience JVM shutdown initiated by Surefire before JTeC is done writing its test report to disk.
This will likely manifest in a JVM "crash" being reported by Maven Surefire.
In those cases, you can simply increase [Surefire's exit timeout timespan](https://maven.apache.org/surefire/maven-surefire-plugin/test-mojo.html#forkedProcessExitTimeoutInSeconds) by setting `-Dsurefire.exitTimeout=150` (default are 30 seconds).
