package de.junit5project;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleTest extends AbstractTestSuperClass {

    @Test
    void shouldReturnTrue() {
        assertTrue(true);
    }

    @Test
    void shouldReturnFalse() {
        assertFalse(false);
    }
}
