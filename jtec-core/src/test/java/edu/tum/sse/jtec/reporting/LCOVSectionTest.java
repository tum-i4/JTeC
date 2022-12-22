package edu.tum.sse.jtec.reporting;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LCOVSectionTest {
    @Test
    void shouldConvertToLcovFormattedString() {
        // given
        LCOVSection lcovSection = new LCOVSection("test-suite", "path/to/source_file");
        lcovSection.addMethod(5 ,"foo");
        lcovSection.addMethodHit(1, "foo");
        lcovSection.addLineHit(5, 1);
        lcovSection.addLineHit(6, 1);
        lcovSection.addLineHit(7, 1);

        // when
        String lcovString = lcovSection.toString();

        // then
        assertEquals("TN:test-suite\n" +
                "SF:path/to/source_file\n" +
                "FN:5,foo\n" +
                "FNDA:1,foo\n" +
                "FNF:1\n" +
                "FNH:1\n" +
                "DA:5,1\n" +
                "DA:6,1\n" +
                "DA:7,1\n" +
                "LF:3\n" +
                "LH:3\n" +
                "end_of_record\n", lcovString);
    }
}