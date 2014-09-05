package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.client.render.PatchOpExecutor;
import org.activityinfo.ui.vdom.shared.dom.DomNode;

public class RemoveOp implements PatchOp {

    public static final RemoveOp INSTANCE = new RemoveOp();

    private RemoveOp() {}

    @Override
    public DomNode apply(PatchOpExecutor executor, DomNode domNode) {
        return executor.removeNode(domNode);
    }

    @Override
    public String toString() {
        return "[REMOVE]";
    }
}
