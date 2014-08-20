package org.activityinfo.ui.vdom.shared.tree;

import org.activityinfo.ui.vdom.client.render.RenderContext;

public abstract class VThunk<T> extends VTree {


    private RenderContext context = null;



    /**
     * Cached result of the last call to render
     */
    public VTree vNode = null;


    /**
     * True if this thunk needs to be re-rendered on the next pass
     */
    private boolean dirty = false;


    /**
     * Marks this node as dirty
     */
    protected final void forceUpdate() {
        assert context != null : "No render context is set";
        dirty = true;
        context.fireUpdate(this);
    }

    public final boolean isDirty() {
        return dirty;
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


    /**
     * Called immediately after the thunk is newly added to the real
     * DOM tree.
     */
    public void onMounted() {

    }

    public boolean update(T newValue) {
        return true;
    }


    /**
     * @return the event mask to sink
     * @see com.google.gwt.user.client.Event
     */
    public int getEventMask() {
        return 0;
    }

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
