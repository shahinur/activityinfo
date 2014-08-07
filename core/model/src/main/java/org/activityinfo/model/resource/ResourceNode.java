package org.activityinfo.model.resource;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
    private String label;
    private final List<ResourceNode> children = Lists.newArrayList();

    public ResourceNode(ResourceId id, ResourceId classId) {
        this.id = id;
        this.classId = classId;
    }

    public ResourceNode(JsonObject jsonObject) {
        this.id = ResourceId.create(jsonObject.get("id").getAsString());
        if(jsonObject.has("classId")) {
            this.classId = ResourceId.create(jsonObject.get("classId").getAsString());
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

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id.asString());
        if(classId != null) {
            jsonObject.addProperty("classId", classId.asString());
        }
        if(!children.isEmpty()) {
            JsonArray childArray = new JsonArray();
            for (ResourceNode child : children) {
                childArray.add(child.toJson());
            }
            jsonObject.add("children", childArray);
        }
        return jsonObject;
    }
}
