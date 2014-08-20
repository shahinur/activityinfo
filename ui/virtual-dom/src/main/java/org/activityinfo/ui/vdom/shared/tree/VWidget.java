package org.activityinfo.ui.vdom.shared.tree;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * A virtual node that serves a placeholder for a GWT widget.
 */
public abstract class VWidget extends VTree {

    public static boolean isWidget(VTree a) {
        return a instanceof VWidget;
    }

    /**
     * Called when the Widget is added to the tree for the first time.
     *
     * @return a new GWT widget instance.
     */
    public abstract IsWidget createWidget();

    @Override
    public void accept(VTreeVisitor visitor) {
        visitor.visitWidget(this);
    }
}
