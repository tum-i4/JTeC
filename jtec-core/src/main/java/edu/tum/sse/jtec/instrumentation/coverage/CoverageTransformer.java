package edu.tum.sse.jtec.instrumentation.coverage;

import edu.tum.sse.jtec.instrumentation.coverage.interceptors.ClassCoverageAdvice;
import edu.tum.sse.jtec.instrumentation.coverage.interceptors.MethodCoverageAdvice;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class CoverageTransformer implements AgentBuilder.Transformer {

    /**
     * Resources that need to be present in the current ClassLoader for the target to be
     * instrumented. This might be useful in future use cases for collecting distributed coverage.
     */
    private final List<String> classLoaderDependencies;
    private final CoverageLevel coverageLevel;

    private CoverageTransformer(CoverageLevel coverageLevel, List<String> classLoaderDependencies) {
        this.classLoaderDependencies = classLoaderDependencies;
        this.coverageLevel = coverageLevel;
    }

    public static AgentBuilder.Transformer create(CoverageLevel coverageLevel) {
        return create(coverageLevel, Collections.emptyList());
    }

    public static AgentBuilder.Transformer create(
            CoverageLevel coverageLevel, List<String> classLoaderDependencies) {
        return new CoverageTransformer(coverageLevel, classLoaderDependencies);
    }

    @Override
    public DynamicType.Builder<?> transform(
            DynamicType.Builder<?> builder,
            TypeDescription typeDescription,
            ClassLoader classLoader,
            JavaModule module) {

        if (!dependenciesPresent(classLoader)) {
            return builder;
        }

        if (coverageLevel == CoverageLevel.METHOD_LEVEL) {
            return builder.visit(
                    Advice.withCustomMapping()
                            .bind(ClassName.class, typeDescription.getName())
                            .to(MethodCoverageAdvice.class)
                            .on(ElementMatchers.isMethod().or(ElementMatchers.isConstructor())));
        } else if (coverageLevel == CoverageLevel.CLASS_LEVEL) {
            return builder.visit(
                    Advice.withCustomMapping()
                            .bind(ClassName.class, typeDescription.getName())
                            .to(ClassCoverageAdvice.class)
                            .on(ElementMatchers.isStatic().or(ElementMatchers.isConstructor().or(ElementMatchers.isTypeInitializer()))));
        } else {
            return builder;
        }
    }

    private boolean dependenciesPresent(ClassLoader classLoader) {
        for (String dependency : classLoaderDependencies) {
            if (!isAccessibleFromClassLoader(classLoader, dependency)) {
                System.err.println(
                        "Skipping instrumentation. Could not find the following dependency in ClassLoader: "
                                + dependency);
                return false;
            }
        }
        return true;
    }

    private static boolean isAccessibleFromClassLoader(ClassLoader loader, String resource) {
        if (loader == null) {
            return false;
        }
        try (InputStream inputStream = loader.getResourceAsStream(resource)) {
            if (inputStream == null) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

