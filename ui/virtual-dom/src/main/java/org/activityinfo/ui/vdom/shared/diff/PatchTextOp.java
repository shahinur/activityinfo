package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.client.render.PatchOpExecutor;
import org.activityinfo.ui.vdom.shared.dom.DomNode;

public class PatchTextOp implements PatchOp {

    private final String text;

    public PatchTextOp(String text) {
        this.text = text;
    }

    @Override
    public DomNode apply(PatchOpExecutor executor, DomNode domNode) {
        return executor.patchText(domNode, text);
    }

    @Override
    public String toString() {
        return "[TEXT -> " + text + "]";
    }
}
