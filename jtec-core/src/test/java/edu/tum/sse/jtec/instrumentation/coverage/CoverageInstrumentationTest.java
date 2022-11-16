package edu.tum.sse.jtec.instrumentation.coverage;

import edu.tum.sse.jtec.instrumentation.coverage.test.Foo;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ByteArrayClassLoader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class CoverageInstrumentationTest {

    private final Instrumentation instrumentation = ByteBuddyAgent.install();
    CoverageMonitor coverageMonitorSpy;
    private Path tmpDir;
    private ClassLoader classLoader;

    private static void checkJVMRequirements() {
        final Instrumentation instrumentation = ByteBuddyAgent.install(ByteBuddyAgent.AttachmentProvider.DEFAULT);
        if (!instrumentation.isRedefineClassesSupported()) {
            throw new RuntimeException("The executing JVM does not support class redefinition");
        }
        if (!instrumentation.isRetransformClassesSupported()) {
            throw new RuntimeException("The executing JVM does not support class retransformation");
        }
    }

    @BeforeEach
    void setUp() throws IOException {
        tmpDir = Files.createTempDirectory("tmpDirPrefix");
        coverageMonitorSpy = spy(CoverageMonitor.create());

        classLoader = new ByteArrayClassLoader.ChildFirst(
                this.getClass().getClassLoader(),
                ClassFileLocator.ForClassLoader.readToNames(Foo.class),
                ByteArrayClassLoader.PersistenceHandler.MANIFEST);

        checkJVMRequirements();
    }

    @AfterEach
    void tearDown() {
        tmpDir.toFile().delete();
    }

    @Test
    void shouldInstrumentClassLevel() throws ClassNotFoundException {
        // given
        final String fooClass = Foo.class.getName();
        final CoverageLevel coverageLevel = CoverageLevel.CLASS;

        assertEquals(fooClass, classLoader.loadClass(fooClass).getName());

        // when
        CoverageInstrumentation instr = null;
        try (final MockedStatic<CoverageMonitor> monitorMockedStatic = mockStatic(CoverageMonitor.class)) {
            monitorMockedStatic.when(CoverageMonitor::create).thenReturn(coverageMonitorSpy);
            instr = new CoverageInstrumentation(
                    tmpDir.resolve("cov.log").toAbsolutePath().toString(),
                    coverageLevel,
                    fooClass,
                    "",
                    true,
                    PIDStrategy.getInstance()
            );
            instr.attach(instrumentation, tmpDir.toFile());
            final Class<?> fooType = classLoader.loadClass(fooClass);
            fooType.getDeclaredConstructor().newInstance();
        } catch (final Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if (instr != null) {
                instr.reset();
            }
        }

        // then
        verify(coverageMonitorSpy, atLeast(1)).registerClass(fooClass);
    }

    @Test
    void shouldInstrumentMethodLevel() throws ClassNotFoundException {
        // given
        final String fooClass = Foo.class.getName();
        final CoverageLevel coverageLevel = CoverageLevel.METHOD;

        assertEquals(fooClass, classLoader.loadClass(fooClass).getName());

        // when
        CoverageInstrumentation instr = null;
        try (final MockedStatic<CoverageMonitor> monitorMockedStatic = mockStatic(CoverageMonitor.class)) {
            monitorMockedStatic.when(CoverageMonitor::create).thenReturn(coverageMonitorSpy);
            instr = new CoverageInstrumentation(
                    tmpDir.resolve("cov.log").toAbsolutePath().toString(),
                    coverageLevel,
                    fooClass,
                    "",
                    true,
                    PIDStrategy.getInstance()
            );
            instr.attach(instrumentation, tmpDir.toFile());
            final Class<?> fooType = classLoader.loadClass(fooClass);
            fooType.getDeclaredMethod("foo").invoke(fooType.getDeclaredConstructor().newInstance());
        } catch (final Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            if (instr != null) {
                instr.reset();
            }
        }

        // then
        verify(coverageMonitorSpy, atLeast(1)).registerMethodCall(fooClass, "<init>()", "void");
        verify(coverageMonitorSpy, times(1)).registerMethodCall(fooClass, "foo()", "java.lang.String");
    }

}
