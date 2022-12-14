package edu.tum.sse.jtec.reporting;

import java.nio.file.Path;
import java.util.List;

public class LCOVReport {
    private List<LCOVSection> lcovSections;

    public LCOVReport(List<LCOVSection> lcovSections) {
        this.lcovSections = lcovSections;
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

    public List<LCOVSection> getLcovSections() {
        return lcovSections;
    }

    public void setLcovSections(List<LCOVSection> lcovSections) {
        this.lcovSections = lcovSections;
    }
}
