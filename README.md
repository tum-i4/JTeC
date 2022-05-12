# JTeC: Java Testwise Coverage

JTeC is a tool that enables researchers to obtain testwise coverage for programs that run in the JVM.
It targets research in regression testing, such as test selection, prioritization, or flaky test analysis.
To analyze tests beyond code coverage, JTeC also supports instrumenting APIs that open external files (e.g., `java.io` and `java.nio`), 
connect to sockets (e.g., `java.net.socket`), or spawn threads or processes (e.g., `java.lang`).

## Acknowledgments & Contributors

The project is developed as part of the BMBF funded project Q-Soft by [CQSE](https://www.cqse.eu/en/) and [TUM](https://www.in.tum.de/i04/).
JTeC is mainly developed by Raphael Noemmer and Daniel Elsner.

## Usage

TBD

## Structure

```
├── jtec-agent          <- The JTeC agent is a simple Java agent that can be parameterized and attached to a JVM process.
├── jtec-core           <- The JTeC core package contains instrumentation and auxiliary code.
└── jtec-maven-plugin   <- The JTeC maven plugin provides a utility to attach the agent to the tests of a Maven project.
```

## Setup

TBD
