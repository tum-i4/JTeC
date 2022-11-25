package de.junit5project;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(LoggingExtension.class)
public class LoggingExtensionTest {

    private Logger logger;

    @Test
    void shouldHaveCorrectLogger() {
        logger.info("Correct logger set up!");
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
