package edu.tum.sse.jtec.instrumentation.coverage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Simple concurrent data structure to store coverage probes.
 */
public class CoverageMap {

    /**
     * Current coverage probes that have been recorded.
     */
    private final Set<String> currentCoverage = ConcurrentHashMap.newKeySet();

    /**
     * Stores a set of covered probes for each dump ID (e.g., PID).
     */
    private final Map<String, Set<String>> collectedProbes = new HashMap<>();

    public CoverageMap put(String value) {
        currentCoverage.add(value);
        return this;
    }

    public void clear() {
        currentCoverage.clear();
        collectedProbes.clear();
    }

    public Map<String, Set<String>> getCollectedProbes() {
        return collectedProbes;
    }

    public void dump(String dumpId) {
        collectedProbes.put(dumpId, new HashSet<>(currentCoverage));
        currentCoverage.clear();
    }
}
