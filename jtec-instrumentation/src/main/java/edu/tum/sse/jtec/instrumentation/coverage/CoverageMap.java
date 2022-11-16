package edu.tum.sse.jtec.instrumentation.coverage;

import java.util.HashSet;
import java.util.Objects;
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

    public CoverageMap put(String key, String value) {
        if (!collectedProbes.containsKey(key)) {
            collectedProbes.putIfAbsent(key, ConcurrentHashMap.newKeySet());
        }
        collectedProbes.get(key).add(value);
        return this;
    }

    public void clear() {
        collectedProbes.clear();
    }

    public ConcurrentMap<String, Set<String>> getCollectedProbes() {
        return collectedProbes;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CoverageMap that = (CoverageMap) o;
        return Objects.equals(collectedProbes, that.collectedProbes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collectedProbes);
    }
}
