package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.client.render.PatchOpExecutor;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.tree.VComponent;

public class PatchComponentOp implements PatchOp {

    private final VComponent previous;
    private final VComponent replacement;
    private final VPatchSet patchSet;

    public PatchComponentOp(VComponent previous, VComponent replacement, VPatchSet patchSet) {
        this.previous = previous;
        this.replacement = replacement;
        this.patchSet = patchSet;
    }

    public PatchComponentOp(VComponent updated, VPatchSet patchSet) {
        this.previous = updated;
        this.replacement = updated;
        this.patchSet = patchSet;
    }

    public VPatchSet getPatchSet() {
        return patchSet;
    }

    public VComponent getPrevious() {
        return previous;
    }

    @Override
    public String toString() {
        return "[UPDATE COMPONENT " + previous.getDebugId() + "]";
    }

    @Override
    public DomNode apply(PatchOpExecutor executor, DomNode domNode) {
        return executor.patchComponent(domNode, previous, replacement, patchSet);
    }
}
