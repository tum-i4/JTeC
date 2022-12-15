package edu.tum.sse.jtec.reporting;

import java.util.HashMap;
import java.util.Map;

/**
 * Section describing one source file in an LCOV report.
 */
public class LCOVSection {
    private final String testName;
    private final String sourceFilePath;

    /**
     * Line numbers and their execution count ("hits") -> `DA`
     */
    private final HashMap<Integer, Integer> lineHits = new HashMap<>();

    /**
     * Line numbers and names of method declarations (`FN`)
     */
    private final HashMap<Integer, String> methods = new HashMap<>();

    /**
     * Names and execution counts of methods (`FNDA`)
     */
    private final HashMap<String, Integer> methodHits = new HashMap<>();

    public LCOVSection(String testName, String sourceFilePath) {
        this.testName = testName;
        this.sourceFilePath = sourceFilePath;
    }

    public String getTestName() {
        return testName;
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void addLineHit(int lineNum, int hitCount) {
        lineHits.put(lineNum, hitCount);
    }

    public void addMethod(int lineNum, String methodName) {
        methods.put(lineNum, methodName);
    }

    public void addMethodHit(int hitCount, String methodName) {
        methodHits.put(methodName, hitCount);
    }

    /**
     * @return The number of lines found in this source file (`LF`)
     */
    public long numLinesFound() {
        return lineHits.size();
    }

    /**
     * @return The number of lines executed at least once in this source file (`LH`)
     */
    public long numLinesHit() {
        return lineHits.entrySet().stream().filter(l -> l.getValue() > 0).count();
    }

    /**
     * @return The number of methods found in this source file (`FNF`)
     */
    public long numMethodsFound() {
        return methods.size();
    }

    /**
     * @return The number of methods executed in this source file (`FNH`)
     */
    public long numMethodsHit() {
        return methodHits.size();
    }

    /**
     * Convert this section into LCOV format.
     * @return The LCOV tracefile as a String
     */
    @Override
    public String toString() {
        StringBuilder lcovString = new StringBuilder();
        lcovString.append("TN:").append(testName).append('\n');
        lcovString.append("SF:").append(sourceFilePath).append('\n');
        for (Map.Entry<Integer, String> method : methods.entrySet()) {
            int lineNum = method.getKey();
            String methodName = method.getValue();
            lcovString.append("FN:").append(lineNum).append(",").append(methodName).append('\n');
        }
        for (Map.Entry<String, Integer> methodHit : methodHits.entrySet()) {
            String methodName= methodHit.getKey();
            int hitCount = methodHit.getValue();
            lcovString.append("FNDA:").append(hitCount).append(",").append(methodName).append('\n');
        }
        lcovString.append("FNF:").append(numMethodsFound()).append('\n');
        lcovString.append("FNH:").append(numMethodsHit()).append('\n');
        for (Map.Entry<Integer, Integer> lineHit : lineHits.entrySet()) {
            int lineNum = lineHit.getKey();
            int hitCount = lineHit.getValue();
            lcovString.append("DA:").append(lineNum).append(",").append(hitCount).append('\n');
        }
        lcovString.append("LF:").append(numLinesFound()).append('\n');
        lcovString.append("LH:").append(numLinesHit()).append('\n');
        lcovString.append("end_of_record\n");
        return lcovString.toString();
    }
}
