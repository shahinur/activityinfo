package org.activityinfo.ui.client.component.form.field.hierarchy;

import org.activityinfo.model.resource.ResourceId;

public class Node {
    private ResourceId id;
    private ResourceId parentId;
    private String label;

    public Node(ResourceId id, ResourceId parentId, String label) {
        assert parentId != null;
        this.id = id;
        this.parentId = parentId;
        this.label = label;
    }

    Node(ResourceId id, String label) {
        this.id = id;
        this.label = label;
    }


    public ResourceId getId() {
        return id;
    }

    public ResourceId getParentId() {
        return parentId;
    }

    public String getLabel() {
        return label;
    }

    public boolean isRoot() {
        return parentId == null;
    }

    @Override
    public String toString() {
        if(isRoot()) {
            return "Node[" + id + "=" + label + "]";
        } else {
            return "Node[" + parentId + "/" + id + "=" + label + "]";
        }
    }
}
