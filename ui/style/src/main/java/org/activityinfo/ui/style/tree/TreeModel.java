package org.activityinfo.ui.style.tree;

import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.List;

public interface TreeModel<T> {

    void requestRootNodes();

    void requestChildren(T node);

    boolean isLeaf(T node);

    Status<List<T>> getRootNodes();

    Status<List<T>> getChildren(T parent);

    String getLabel(T node);

    Icon getIcon(T node, boolean expanded);

    /**
     * Returns an object which can serve as this node's unique key.
     */
    String getKey(T node);

    void addChangeListener(StoreChangeListener listener);

    void removeChangeListener(StoreChangeListener listener);
}
