package edu.tum.sse.jtec.instrumentation.coverage;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Simple concurrent data structure to store coverage probes.
 */
public class CoverageMap {

    /**
     * Stores a set of covered probes for each coverageRunId (e.g., PID).
     */
    private final ConcurrentMap<String, Set<String>> collectedProbes = new ConcurrentHashMap<>();

    public void put(String key, String value) {
        if (!collectedProbes.containsKey(key)) {
            collectedProbes.putIfAbsent(key, ConcurrentHashMap.newKeySet());
        }
        collectedProbes.get(key).add(value);
    }

    public boolean remove(String key, String value) {
        Set<String> set = collectedProbes.get(key);
        if (set != null) {
            return set.remove(value);
        }
        return false;
    }

    public boolean contains(String key, String value) {
        return collectedProbes.containsKey(key) && collectedProbes.get(key).contains(value);
    }

    public void clear() {
        collectedProbes.clear();
    }

    public ConcurrentMap<String, Set<String>> getCollectedProbes() {
        return collectedProbes;
    }
}
