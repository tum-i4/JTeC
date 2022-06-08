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
        GlobalCoverageMonitor.get().registerMethodCall(className, methodSignature, returnType);
    }
}
