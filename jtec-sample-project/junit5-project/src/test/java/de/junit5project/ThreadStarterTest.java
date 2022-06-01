package de.junit5project;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThreadStarterTest {

    @Test
    public void testDoSomething() {
        final ThreadStarter app = new ThreadStarter();
        assertTrue(app.doSomething());
    }
}
