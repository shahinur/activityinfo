package org.activityinfo.model.type.enumerated;

import org.activityinfo.model.resource.ResourceId;

public class EnumValue {
    private ResourceId id;
    private String label;

    public EnumValue(ResourceId id, String label) {
        this.id = id;
        this.label = label;
    }

    public ResourceId getId() {
        return id;
    }

    public void setId(ResourceId id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
