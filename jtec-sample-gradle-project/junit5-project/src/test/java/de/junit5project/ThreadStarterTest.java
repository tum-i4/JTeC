package de.junit5project;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(LifecycleExtension.class)
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
