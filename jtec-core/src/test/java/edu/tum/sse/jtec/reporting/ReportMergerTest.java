package edu.tum.sse.jtec.reporting;

import com.google.gson.JsonParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static edu.tum.sse.jtec.util.JSONUtils.toJson;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReportMergerTest {

    static Path inputDirectory;
    static Path invalidTraces;
    static Path newTraces;
    static Path oldTraces;

    @BeforeAll
    static void beforeAll() throws URISyntaxException {
        inputDirectory = Paths.get(Objects.requireNonNull(ReportMergerTest.class.getResource("/update-traces")).toURI()).toAbsolutePath();
        invalidTraces = inputDirectory.resolve("old-invalid-traces.json");
        oldTraces = inputDirectory.resolve("old-traces.json");
        newTraces = inputDirectory.resolve("new-traces.json");
    }

    private boolean testReportEquals(TestReport testReport1, TestReport testReport2) {
        for (TestSuite ts1 : testReport1.getTestSuites()) {
            boolean testSuiteFound = false;
            for (TestSuite ts2 : testReport2.getTestSuites()) {
                if (ts1.getTestId().equals(ts2.getTestId())) {
                    for (String of1 : ts1.getOpenedFiles()) {
                        boolean fileFound = false;
                        for (String of2 : ts2.getOpenedFiles()) {
                            if (of1.equals(of2)) {
                                fileFound = true;
                                break;
                            }
                        }
                        if (!fileFound) {
                            return false;
                        }
                    }
                    for (String ce1 : ts1.getCoveredEntities()) {
                        boolean entityFound = false;
                        for (String ce2 : ts2.getCoveredEntities()) {
                            if (ce1.equals(ce2)) {
                                entityFound = true;
                                break;
                            }
                        }
                        if (!entityFound) {
                            return false;
                        }
                    }
                    testSuiteFound = true;
                    break;
                }
            }
            if (!testSuiteFound) {
                return false;
            }
        }
        return true;
    }

    @Test
    void shouldThrowParsingInvalidFormat() {
        ReportMerger merger = new ReportMerger(invalidTraces, invalidTraces, true);
        assertThrows(RuntimeException.class, merger::merge);
    }

    @Test
    void shouldReturnOldTracesIfNewTracesAreInvalid() {
        ReportMerger merger = new ReportMerger(oldTraces, invalidTraces, true);
        List<TestSuite> expectedTestSuites = new ArrayList<>();
        TestSuite oldTestSuite = new TestSuite();
        oldTestSuite.setTestId("a.b.c.FooTest");
        oldTestSuite.setCoveredEntities(Collections.singleton("a.b.c.Foo"));
        expectedTestSuites.add(oldTestSuite);
        TestSuite newTestSuite = new TestSuite();
        newTestSuite.setTestId("a.b.c.BarTest");
        newTestSuite.setOpenedFiles(Collections.singleton("log5j.xml"));
        expectedTestSuites.add(newTestSuite);
        TestReport expected = new TestReport(
                "report",
                0,
                0,
                expectedTestSuites
        );

        // when
        TestReport actual = merger.merge();

        // then
        assertTrue(testReportEquals(expected, actual), String.format("%s\n!=\n%s", toJson(expected), toJson(actual)));
    }

    @Test
    void shouldMergeOldAndNew() {
        // given
        ReportMerger merger = new ReportMerger(oldTraces, newTraces, true);
        List<TestSuite> expectedTestSuites = new ArrayList<>();
        TestSuite oldTestSuite = new TestSuite();
        oldTestSuite.setTestId("a.b.c.FooTest");
        oldTestSuite.setCoveredEntities(Collections.singleton("a.b.c.Foo"));
        oldTestSuite.setOpenedFiles(Collections.singleton("log4j.xml"));
        expectedTestSuites.add(oldTestSuite);
        TestSuite newTestSuite = new TestSuite();
        newTestSuite.setTestId("a.b.c.BarTest");
        newTestSuite.setOpenedFiles(Collections.singleton("log5j.xml"));
        expectedTestSuites.add(newTestSuite);
        TestReport expected = new TestReport(
                "report",
                0,
                0,
                expectedTestSuites
        );

        // when
        TestReport actual = merger.merge();

        // then
        assertTrue(testReportEquals(expected, actual), String.format("%s\n!=\n%s", toJson(expected), toJson(actual)));
    }

    @Test
    void shouldUpdateAlsoFailingTests() {
        // given
        ReportMerger merger = new ReportMerger(oldTraces, newTraces, false);
        List<TestSuite> expectedTestSuites = new ArrayList<>();
        TestSuite oldTestSuite = new TestSuite();
        oldTestSuite.setTestId("a.b.c.FooTest");
        oldTestSuite.setCoveredEntities(Collections.singleton("a.b.c.Foo"));
        oldTestSuite.setOpenedFiles(Collections.singleton("log4j.xml"));
        expectedTestSuites.add(oldTestSuite);
        TestSuite newTestSuite = new TestSuite();
        newTestSuite.setTestId("a.b.c.BarTest");
        newTestSuite.setCoveredEntities(Collections.singleton("a.b.c.Bar"));
        newTestSuite.setOpenedFiles(Collections.singleton("log5j.xml"));
        expectedTestSuites.add(newTestSuite);
        TestReport expected = new TestReport(
                "report",
                0,
                0,
                expectedTestSuites
        );

        // when
        TestReport actual = merger.merge();

        // then
        assertTrue(testReportEquals(expected, actual), String.format("%s\n!=\n%s", toJson(expected), toJson(actual)));
    }
}