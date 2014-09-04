package org.activityinfo.model.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import org.activityinfo.model.system.FolderClass;

import java.util.List;

/**
 * Represents a node within the Resource Tree
 *
 * Resources are organized in a hierarchy of ownership, where a Resource
 * can "own" zero or more other resources. Access rules are inherited from
 * their owner in a structure intended to look like a familiar hierarchical
 * system of folders.
 *
 */
public class ResourceNode {

    private ResourceId id;
    private ResourceId classId;
    private ResourceId ownerId;
    private String label;
    private long version;
    private long subTreeVersion;
    private List<ResourceNode> children = Lists.newArrayList();
    private boolean parent;

    @JsonCreator
    public ResourceNode(@JsonProperty("id") ResourceId id) {
        this.id = id;
    }

    public ResourceNode(ResourceId id, ResourceId classId) {
        this.id = id;
        this.classId = classId;
    }

    public ResourceNode(Resource resource) {
        this.id = resource.getId();
        this.label = resource.getString(FolderClass.LABEL_FIELD_ID.asString());
        this.ownerId = resource.getOwnerId();
        this.classId = ResourceId.valueOf(resource.getString("classId"));
        this.version = resource.getVersion();
    }

    /**
     * @return the {@code Resource}'s id
     */
    public ResourceId getId() {
        return id;
    }

    public void setId(ResourceId id) {
        this.id = id;
    }

    public ResourceId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ResourceId ownerId) {
        this.ownerId = ownerId;
    }

    /**
     *
     * @return the id of this {@code Resource}'s {@code FormClass}, if this Resource
     * has a FormClassId, or {@code null} otherwise
     */
    public ResourceId getClassId() {
        return classId;
    }

    public void setClassId(ResourceId classId) {
        this.classId = classId;
    }

    /**
     *
     * @return this form's label
     */
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<ResourceNode> getChildren() {
        return children;
    }

    public void setChildren(List<ResourceNode> children) {
        this.children = children;
    }

    /**
     *
     * @return this Resource's current version number
     */
    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    /**
     *
     * @return the most recent version number of this Resource or any of it's descendants.
     */
    public long getSubTreeVersion() {
        return subTreeVersion;
    }

    public void setSubTreeVersion(long subTreeVersion) {
        this.subTreeVersion = subTreeVersion;
    }

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    private void appendTo(StringBuilder sb, String indent) {
        sb.append(indent).append("[ ").append(label)
                .append(" id=").append(id)
                .append(" classId=").append(classId);

        if(children.isEmpty()) {
            sb.append("]");
        } else {
            for (ResourceNode child : children) {
                child.appendTo(sb, indent + "   ");
                sb.append("\n");
            }
            sb.append(indent).append("]\n");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendTo(sb, "");
        return sb.toString();
    }
}
