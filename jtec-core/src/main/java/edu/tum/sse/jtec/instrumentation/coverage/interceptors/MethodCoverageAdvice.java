package edu.tum.sse.jtec.instrumentation.coverage.interceptors;

import edu.tum.sse.jtec.instrumentation.coverage.ClassName;
import edu.tum.sse.jtec.instrumentation.coverage.GlobalCoverageMonitor;
import net.bytebuddy.asm.Advice;

public class MethodCoverageAdvice {

    @Advice.OnMethodEnter
    public static void enter(
            @Advice.Origin("#m#s") String methodSignature,
            @Advice.Origin("#r") String returnType,
            @ClassName String className) {
        // Because this method is prepended and will run to the instrumented method
        // this will return the actual runtime class, not "MethodCoverageAdvice"
        GlobalCoverageMonitor.get().registerMethodCall(className, methodSignature, returnType);
    }
}
