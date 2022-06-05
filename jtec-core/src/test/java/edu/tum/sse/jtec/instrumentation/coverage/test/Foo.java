package edu.tum.sse.jtec.instrumentation.coverage.test;

// Test classes are in another package because this way we can use
// cov.include=edu.tum.sse.jtec.instrumentation.coverage.test.*
// without having the problem of instrumenting also the classes of the agent.

public class Foo {
    public String foo() {
        return "foo";
    }
}

class Bar {
    public String bar() {
        return new Foo().foo();
    }
}
