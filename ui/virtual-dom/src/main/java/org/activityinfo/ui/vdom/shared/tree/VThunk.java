package org.activityinfo.ui.vdom.shared.tree;

import com.google.gwt.user.client.Event;
import org.activityinfo.ui.vdom.client.render.RenderContext;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class VThunk<T> extends VTree {

    private static final Logger LOGGER = Logger.getLogger(VThunk.class.getName());

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


    public boolean shouldUpdate(T previousProperties) {
        return true;
    }


    /**
     * Renders this Thunk to a concrete {@code VTree}. During diffing,
     * a {@code previous} parameter provides the previously rendered thunk
     * from the last {@code VTree}. This thunk can decide whether to reuse
     * the {@code VTree} from {@code previous} or re-render.
     *
     * @return a {@code VTree} node of type {@code VNode}, {@code VText} or {@code VWidget}
     */
    protected abstract VTree render();


    /**
     * Called immediately after the thunk is newly added to the real
     * DOM tree.
     */
    public void onMounted() {

    }


    /**
     * @return the event mask to sink
     * @see com.google.gwt.user.client.Event
     */
    public int getEventMask() {
        return 0;
    }

    public void onBrowserEvent(Event event) {

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
                if(previousThunk.vNode == null || dirty || safeShouldUpdate(previousThunk)) {
                    vNode = render();
                } else {
                    vNode = previousThunk.vNode;
                }
                return vNode;
            }

            // just render
            return render();
        }
    }

    private boolean safeShouldUpdate(VThunk previousThunk) {
        try {
            return shouldUpdate((T) previousThunk);
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while calling shouldUpdate()", e);
            return true;
        }
    }

    @Override
    public VTree force() {
        if(vNode == null) {
            vNode = render();
        }
        return vNode;
    }
}
