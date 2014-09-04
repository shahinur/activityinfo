package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.client.render.PatchOpExecutor;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import javax.annotation.Nonnull;

public class ReplaceOp implements PatchOp {

    @Nonnull
    private final VTree newNode;

    public ReplaceOp(VTree newNode) {
        this.newNode = newNode;
    }

    @Override
    public DomNode apply(PatchOpExecutor executor, DomNode domNode) {
        return executor.replaceNode(domNode, newNode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReplaceOp replaceOp = (ReplaceOp) o;

        if (!newNode.equals(replaceOp.newNode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return newNode.hashCode();
    }

    @Override
    public String toString() {
        return "[REPLACE WITH" + newNode + "]";
    }
}
