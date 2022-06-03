package edu.tum.sse.jtec.instrumentation;

import java.lang.instrument.Instrumentation;

/**
 * Base class for instrumentations.
 */
public abstract class AbstractInstrumentation<T> {
    protected final String outputPath;

    protected AbstractInstrumentation(String outputPath) {
        this.outputPath = outputPath;
    }

    public abstract void reset();

    /**
     * Attaches another instrumentation to the JVM instrumentation object.
     * This method is designed to allow chaining of calls (it returns this object).
     * @param instrumentation The JVM instrumentation object.
     * @return this
     */
    public abstract T attach(Instrumentation instrumentation);
}
