package de.junit4project;


import org.junit.Test;

import static org.junit.Assert.assertTrue;

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
