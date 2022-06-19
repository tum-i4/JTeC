package de.junit5project;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreadStarterTest {

    @Test
    public void testDoSomething() throws InterruptedException {
        assertTrue(ThreadStarter.doSomething());
    }

    @Test
    public void testDoSomethingElse() {
        assertTrue(true);
    }
}
