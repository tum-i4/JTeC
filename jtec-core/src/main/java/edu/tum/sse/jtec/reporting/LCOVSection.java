package edu.tum.sse.jtec.reporting;

import java.util.HashMap;
import java.util.Map;

/**
 * Section describing one source file in an LCOV report.
 */
public class LCOVSection {
    private String testName;
    private String sourceFilePath;

    /**
     * Line numbers and their execution count ("hits")
     */
    private final HashMap<Integer, Integer> lineHits = new HashMap<>();

    public void addLineHit(int lineNum, int hitCount) {
        lineHits.put(lineNum, hitCount);
    }

    /**
     * @return The number of instrumented lines in this source file (`LF`)
     */
    public long numLinesInstrumented() {
        return lineHits.size();
    }

    /**
     * @return The number of lines executed at least once in this source file (`LH`)
     */
    public long numLinesHit() {
        return lineHits.entrySet().stream().filter(l -> l.getValue() > 0).count();
    }

    /**
     * Convert the section into LCOV format.
     * @return The LCOV string
     */
    @Override
    public String toString() {
        StringBuilder lcovString = new StringBuilder();
        lcovString.append("TN:").append(testName).append('\n');
        lcovString.append("SF:").append(sourceFilePath).append('\n');
        for (Map.Entry<Integer, Integer> lineHit : lineHits.entrySet()) {
            int lineNum = lineHit.getKey();
            int hitCount = lineHit.getValue();
            lcovString.append("DA:").append(lineNum).append(",").append(hitCount).append('\n');
        }
        lcovString.append("LF:").append(numLinesInstrumented()).append('\n');
        lcovString.append("LH:").append(numLinesHit()).append('\n');
        lcovString.append("end_of_record\n");
        return lcovString.toString();
    }
}
