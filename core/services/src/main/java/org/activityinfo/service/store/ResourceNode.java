package org.activityinfo.service.store;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.activityinfo.model.resource.ResourceId;

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
    private final List<ResourceNode> children = Lists.newArrayList();
    private boolean parent;

    public ResourceNode(ResourceId id) {
        this.id = id;
    }

    public ResourceNode(ResourceId id, ResourceId classId) {
        this.id = id;
        this.classId = classId;
    }

    public ResourceNode(JsonObject jsonObject) {
        this.id = ResourceId.valueOf(jsonObject.get("id").getAsString());
        if(jsonObject.has("classId")) {
            this.classId = ResourceId.valueOf(jsonObject.get("classId").getAsString());
        }
        if(jsonObject.has("children")) {
            JsonArray childArray = jsonObject.get("children").getAsJsonArray();
            for(int i=0;i!=childArray.size();++i) {
                this.children.add(new ResourceNode(childArray.get(i).getAsJsonObject()));
            }
        }
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

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id.asString());
        jsonObject.addProperty("label", label);
        jsonObject.addProperty("version", version);
        jsonObject.addProperty("subTreeVersion", subTreeVersion);
        jsonObject.addProperty("classId", classId.asString());

        if(!children.isEmpty()) {
            JsonArray childArray = new JsonArray();
            for (ResourceNode child : children) {
                childArray.add(child.toJson());
            }
            jsonObject.add("children", childArray);
        }
        return jsonObject;
    }

    public static ResourceNode fromJson(JsonObject jsonObject) {
        ResourceId id = ResourceId.valueOf(jsonObject.get("id").getAsString());
        ResourceId classId = ResourceId.valueOf(jsonObject.get("classId").getAsString());
        ResourceNode node = new ResourceNode(id, classId);
        node.setLabel(jsonObject.get("label").getAsString());
        node.setVersion(jsonObject.get("version").getAsLong());
        node.setSubTreeVersion(jsonObject.get("subTreeVersion").getAsLong());

        if(jsonObject.has("children")) {
            JsonArray childArray = jsonObject.get("children").getAsJsonArray();
            for(int i=0;i!=childArray.size();++i) {
                node.children.add(ResourceNode.fromJson(childArray.get(i).getAsJsonObject()));
            }
        }

        return node;
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
