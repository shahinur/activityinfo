package org.activityinfo.model.analysis;

import com.google.common.collect.Sets;

import java.util.Set;

public class CategoryMapping {
    private String label;
    private boolean valid;
    private Set<String> sources;

    public CategoryMapping(String label, String... sources) {
        this.label = label;
        this.sources = Sets.newHashSet(sources);
    }

    public String getLabel() {
        return label;
    }

    public boolean isValid() {
        return valid;
    }

    public Set<String> getSources() {
        return sources;
    }
}
