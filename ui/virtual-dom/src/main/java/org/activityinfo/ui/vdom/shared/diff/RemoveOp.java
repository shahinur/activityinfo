package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.client.render.PatchOpExecutor;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class RemoveOp implements PatchOp {

    private VTree node;

    public RemoveOp(VTree node) {
        this.node = node;
    }

    @Override
    public DomNode apply(PatchOpExecutor executor, DomNode domNode) {
        return executor.removeNode(node, domNode);
    }

    @Override
    public String toString() {
        return "[REMOVE " + node + "]";
    }
}
