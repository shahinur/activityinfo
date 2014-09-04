package org.activityinfo.model.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * Partial view of a set of {@code Resource}s, organized into a
 * tree by their owner property.
 */
public class FolderProjection {

    @Nullable
    private ResourceNode rootNode;

    @JsonCreator
    public FolderProjection(@JsonProperty("rootNode") ResourceNode rootNode) {
        this.rootNode = rootNode;
    }

    /**
     *
     * @return the root of the requested tree.
     */
    public ResourceNode getRootNode() {
        return rootNode;
    }
}
