package edu.tum.sse.jtec.instrumentation.coverage;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.regex.Pattern;

public class LoadedClassFileMonitor implements ClassFileTransformer {
    private final Pattern includePattern;
    private final Pattern excludePattern;

    public LoadedClassFileMonitor(String includePattern, String excludePattern) {
        this.includePattern = Pattern.compile(includePattern);
        this.excludePattern = Pattern.compile(excludePattern);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        // Do not redefine classes.
        if (classBeingRedefined != null) {
            return null;
        }

        // Only classes associated with a CodeSource location should be monitored.
        if (protectionDomain.getCodeSource().getLocation() == null) {
            return null;
        }

        if (includePattern.matcher(className).matches() && !excludePattern.matcher(className).matches()) {
            GlobalCoverageMonitor.get().registerClass(className);
        }
        return null;
    }
}
