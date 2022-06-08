package edu.tum.sse.jtec.instrumentation.coverage.interceptors;

import edu.tum.sse.jtec.instrumentation.coverage.ClassName;
import edu.tum.sse.jtec.instrumentation.coverage.GlobalCoverageMonitor;
import net.bytebuddy.asm.Advice;

public class ClassCoverageAdvice {

    @Advice.OnMethodEnter
    public static void enter(@ClassName String className) {
        GlobalCoverageMonitor.get().registerClass(className);
    }
}
