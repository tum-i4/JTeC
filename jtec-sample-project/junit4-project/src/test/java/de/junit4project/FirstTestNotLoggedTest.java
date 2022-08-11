package de.junit4project;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FirstTestNotLoggedTest {

    private static double getValue() {
        return 1.0;
    }

    @Before
    public void setUp() {
        System.out.println("Before!");
    }

    @After
    public void tearDown() {
        System.out.println("After!");
    }

    @Test
    public void testReturnTrue() {
        assertEquals(1.0, getValue(), .001);
    }

    @Test
    public void testReturnFalse() {
        assertEquals(1.0, getValue(), .001);
    }
}
