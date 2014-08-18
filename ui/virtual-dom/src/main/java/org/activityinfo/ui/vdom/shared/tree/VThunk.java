package org.activityinfo.ui.vdom.shared.tree;

public abstract class VThunk extends VTree {

    public VTree vNode = null;

    public static boolean isThunk(VTree a) {
        return a instanceof VThunk;
    }

    /**
     * Renders this Thunk to a concrete {@code VTree}. During diffing,
     * a {@code previous} parameter provides the previously rendered thunk
     * from the last {@code VTree}. This thunk can decide whether to reuse
     * the {@code VTree} from {@code previous} or re-render.
     *
     * @param previous a previous version of this thunk
     * @return a {@code VTree} node of type {@code VNode}, {@code VText} or {@code VWidget}
     */
    protected abstract VTree render(VThunk previous);

    @Override
    public void accept(VTreeVisitor visitor) {
        visitor.visitThunk(this);
    }

    @Override
    public VTree force(VTree previous) {
        if(vNode != null) {
            return vNode;

        } else {
            // have to render, do we have a previous instance?
            if(previous instanceof VThunk) {
                VThunk previousThunk = (VThunk) previous;
                if(previousThunk.vNode != null) {
                    vNode = render(previousThunk);
                    return vNode;
                }
            }

            // just render
            return render(null);
        }
    }

    @Override
    public VTree force() {
        if(vNode == null) {
            vNode = render(null);
        }
        return vNode;
    }
}
