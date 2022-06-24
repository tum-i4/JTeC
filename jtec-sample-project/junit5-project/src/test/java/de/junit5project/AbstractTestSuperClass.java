package de.junit5project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(LifecycleExtension.class)
public abstract class AbstractTestSuperClass {
    @BeforeEach
    public void beforeAll() {
        System.out.println("Before abstract test case");
    }
}
