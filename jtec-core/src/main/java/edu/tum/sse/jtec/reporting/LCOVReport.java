package edu.tum.sse.jtec.reporting;

import java.util.List;
import java.util.Optional;

public class LCOVReport {
    private List<LCOVSection> lcovSections;

    public LCOVReport(List<LCOVSection> lcovSections) {
        this.lcovSections = lcovSections;
    }

    public List<LCOVSection> getLcovSections() {
        return lcovSections;
    }

    public void setLcovSections(List<LCOVSection> lcovSections) {
        this.lcovSections = lcovSections;
    }

    /**
     * Get the LCOV section for the given source file.
     * @param sourceFilePath The path to the source file
     * @return The LCOV section if it exists, else null
     */
    public Optional<LCOVSection> getLcovSection(String sourceFilePath) {
        return lcovSections.stream()
                .filter(l -> l.getSourceFilePath().equals(sourceFilePath))
                .findFirst();
    }

    /**
     * Convert the LCOV report into a tracefile using the LCOV format.
     * @return The LCOV tracefile as a String
     */
    @Override
    public String toString() {
        StringBuilder lcovString = new StringBuilder();
        for (LCOVSection section : lcovSections)
            lcovString.append(section.toString());
        return lcovString.toString();
    }
}
