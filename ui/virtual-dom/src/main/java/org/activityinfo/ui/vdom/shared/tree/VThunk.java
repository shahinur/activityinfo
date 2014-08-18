package org.activityinfo.ui.vdom.shared.tree;

public abstract class VThunk extends VTree {

    public VTree vNode = null;

    public static boolean isThunk(VTree a) {
        return a instanceof VThunk;
    }

    public abstract VTree render(VTree previous);

    @Override
    public void accept(VTreeVisitor visitor) {
        visitor.visitThunk(this);
    }
}
