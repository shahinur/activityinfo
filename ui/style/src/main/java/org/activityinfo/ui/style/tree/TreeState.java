package org.activityinfo.ui.style.tree;

/**
 * Holds the state of the tree's nodes, which
 * you generally want to outlive the TreeComponent
 */
public interface TreeState {

    boolean isExpanded(String key);

    void setExpanded(String key, boolean expanded);

}
