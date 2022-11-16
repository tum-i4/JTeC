package de.junit5project;

import org.junit.jupiter.api.extension.*;

public class LifecycleExtension implements BeforeAllCallback, AfterAllCallback, BeforeEachCallback, AfterEachCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    static {
        init();
    }

    private static void init() {
        System.out.println("Init LifecycleExtension");
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) throws Exception {
        System.out.println("After all: " + extensionContext.getDisplayName());
    }

    @Override
    public void afterTestExecution(final ExtensionContext extensionContext) throws Exception {
        System.out.println("After test execution: " + extensionContext.getDisplayName());
    }

    @Override
    public void beforeAll(final ExtensionContext extensionContext) throws Exception {
        System.out.println("Before all: " + extensionContext.getDisplayName());
    }

    @Override
    public void beforeTestExecution(final ExtensionContext extensionContext) throws Exception {
        System.out.println("Before test execution: " + extensionContext.getDisplayName());
    }

    @Override
    public void afterEach(final ExtensionContext extensionContext) throws Exception {
        System.out.println("After each test: " + extensionContext.getDisplayName());
    }

    @Override
    public void beforeEach(final ExtensionContext extensionContext) throws Exception {
        System.out.println("Before each test: " + extensionContext.getDisplayName());
    }
}
