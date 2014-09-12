package org.activityinfo.ui.style.tree;

import com.google.gwt.user.client.Event;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class TreeNodeIcon<T> extends VComponent {

    private final TreeComponent<T> tree;
    private final T node;
    private final Icon icon;

    public TreeNodeIcon(TreeComponent<T> tree, T node, Icon icon) {
        this.tree = tree;
        this.node = node;
        this.icon = icon;
    }

    @Override
    protected VTree render() {
        return new VNode(HtmlTag.SPAN, PropMap.withClasses("node-icon " + icon.getClassNames()));
    }

    @Override
    public int getEventMask() {
        return Event.ONCLICK;
    }

    @Override
    public void onBrowserEvent(DomEvent event) {
        if(event.getTypeInt() == Event.ONCLICK) {
            event.preventDefault();
            onClick();
        }
    }

    public void onClick() {
        tree.onLabelClicked(node);
    }
}
