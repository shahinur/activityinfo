package org.activityinfo.model.formTree;

import org.activityinfo.model.type.FieldType;

public class ComponentPath {
    private FieldPath fieldPath;
    private String componentId;

    public ComponentPath(FieldPath fieldPath, String componentId) {
        this.fieldPath = fieldPath;
        this.componentId = componentId;
    }

    public ComponentPath(FieldPath fieldPath) {
        this(fieldPath, FieldType.DEFAULT_COMPONENT);
    }

    public FieldPath getFieldPath() {
        return fieldPath;
    }

    public String getComponentId() {
        return componentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComponentPath that = (ComponentPath) o;

        if (componentId != null ? !componentId.equals(that.componentId) : that.componentId != null) {
            return false;
        }
        if (fieldPath != null ? !fieldPath.equals(that.fieldPath) : that.fieldPath != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = fieldPath != null ? fieldPath.hashCode() : 0;
        result = 31 * result + (componentId != null ? componentId.hashCode() : 0);
        return result;
    }
}
