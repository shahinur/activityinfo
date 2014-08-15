package org.activityinfo.model.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Partial view of a set of {@code Resource}s, organized into a
 * tree by their owner property.
 */
public class ResourceTree {

    private ResourceNode rootNode;

    @JsonCreator
    public ResourceTree(@JsonProperty("rootNode") ResourceNode rootNode) {
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
