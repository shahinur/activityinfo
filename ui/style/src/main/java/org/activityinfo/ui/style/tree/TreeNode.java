package org.activityinfo.ui.style.tree;

import com.google.gwt.user.client.Event;
import org.activityinfo.ui.vdom.shared.VDomLogger;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.t;

public class TreeNode<T> extends VComponent {

    private final TreeComponent<T> tree;
    private final T node;
    private final String label;

    public TreeNode(TreeComponent<T> tree, T node, String label) {
        this.label = label;
        this.node = node;
        this.tree = tree;
    }

    @Override
    protected VTree render() {
        return new VNode(HtmlTag.A, new PropMap().set("href", "#"), t(label));
    }

    @Override
    public int getEventMask() {
        return Event.ONCLICK;
    }

    @Override
    public void onBrowserEvent(DomEvent event) {
        if(event.getTypeInt() == Event.ONCLICK) {

            VDomLogger.event(this, "ONCLICK => " + getPropertiesForDebugging());

            event.preventDefault();
            tree.onLabelClicked(node);
        }
    }

    @Override
    public String getPropertiesForDebugging() {
        if(tree == null) {
            return "tree == null";
        } else {
            return "tree = " + (tree.isMounted() ? " m " : " u ") + tree.getDebugIndex();
        }
    }
}
