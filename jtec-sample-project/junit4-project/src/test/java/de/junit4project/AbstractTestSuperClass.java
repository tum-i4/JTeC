package de.junit4project;


import junit.framework.TestCase;
import org.junit.BeforeClass;

public abstract class AbstractTestSuperClass extends TestCase {
    @BeforeClass
    public static void beforeClass() throws Exception {
        System.out.println("Before abstract test suite");
    }
}
