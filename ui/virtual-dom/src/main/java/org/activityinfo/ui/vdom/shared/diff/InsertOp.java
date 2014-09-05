package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.client.render.PatchOpExecutor;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import javax.annotation.Nonnull;


public class InsertOp implements PatchOp {

    @Nonnull
    private final VTree newNode;

    public InsertOp(VTree newNode) {
        this.newNode = newNode;
    }

    @Override
    public DomNode apply(PatchOpExecutor executor, DomNode domNode) {
        return executor.insertNode(domNode, newNode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InsertOp insertOp = (InsertOp) o;

        if (!newNode.equals(insertOp.newNode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return newNode.hashCode();
    }

    @Override
    public String toString() {
        return "[INSERT " + newNode + "]";
    }
}
