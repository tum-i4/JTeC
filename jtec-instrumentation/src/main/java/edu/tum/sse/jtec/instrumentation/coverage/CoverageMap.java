package edu.tum.sse.jtec.instrumentation.coverage;

import java.util.Map;
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
        Set<String> currentSet;
        if (collectedProbes.containsKey(key)) {
            currentSet = collectedProbes.get(key);
        } else {
            currentSet = ConcurrentHashMap.newKeySet();
            collectedProbes.put(key, currentSet);
        }
        currentSet.add(value);
    }

    public void clear() {
        collectedProbes.clear();
    }

    public String toJson() {
        StringBuilder json = new StringBuilder().append('{');
        for (Map.Entry<String, Set<String>> entry: collectedProbes.entrySet()) {
            addInQuotes(json, entry.getKey());
            json.append(":[");
            for (String covered: entry.getValue()) {
                addInQuotes(json, covered);
                json.append(',');
            }
            deleteLast(json);
            json.append("],");
        }
        deleteLast(json);
        json.append('}');

        return json.toString();
    }

    private void addInQuotes(StringBuilder sb, String value) {
        sb.append('"');
        sb.append(value);
        sb.append('"');
    }

    private void deleteLast(StringBuilder sb) {
        sb.deleteCharAt(sb.length() - 1);
    }
}
