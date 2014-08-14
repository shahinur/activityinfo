package org.activityinfo.service.store;

/**
 * Partial view of a set of {@code Resource}s, organized into a
 * tree by their owner property.
 */
public class ResourceTree {

    private ResourceNode rootNode;

    public ResourceTree(ResourceNode rootNode) {
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
