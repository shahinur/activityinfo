package org.activityinfo.ui.vdom.shared.tree;

import org.activityinfo.ui.vdom.client.render.RenderContext;
import org.activityinfo.ui.vdom.shared.VDomLogger;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.dom.DomNode;

public abstract class VComponent<T> extends VTree {

    private int debugIndex = VDomLogger.nextDebugId();

    private RenderContext context = null;

    private DomNode domNode;

    protected VComponent() {
        VDomLogger.event(this, "constructed");
    }

    public final void fireMounted(RenderContext context, DomNode domNode) {
        assert this.domNode != domNode : this + " mounted twice to same dom node";
        assert this.context == null : this + " may only be mounted once";
        this.context = context;
        this.domNode = domNode;

        VDomLogger.event(this, "didMount");

        componentDidMount();
    }

    public final void fireWillUnmount() {
        assert this.context != null : this.getDebugId() +  " must be mounted first";

        VDomLogger.event(this, "willUnmount");

        context.componentUnmounted(this, domNode);

        componentWillUnmount();

        this.context = null;
        this.domNode = null;
    }

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
    public final void refresh() {
        //assert context != null : "No render context is set for " + getDebugId();
        dirty = true;
        if(isMounted()) {
            context.fireUpdate(this);
        }
    }

    public final boolean isDirty() {
        return dirty;
    }


    public final boolean isRendered() {
        return vNode != null;
    }


    public final boolean isMounted() { return domNode != null; }

    @Override
    public boolean hasComponents() {
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
     * Invoked once, both on the client and server, immediately before the initial rendering occurs.
     * If you call setState within this method, render() will see the updated state and will be
     * executed only once despite the state change.
     */
    protected void componentWillMount() {

    }

    /**
     * Called immediately after the thunk is newly added to the real
     * DOM tree.
     */
    protected void componentDidMount() {

    }

    protected void componentWillUnmount() {

    }

    public final DomNode getDomNode() {
        assert domNode != null : "component has not been mounted";
        return domNode;
    }

    protected final RenderContext getContext() {
        assert context != null : "component has not been mounted!";
        return context;
    }


    /**
     * @return the event mask to sink
     * @see com.google.gwt.user.client.Event
     */
    public int getEventMask() {
        return 0;
    }

    public void onBrowserEvent(DomEvent event) {

    }

    @Override
    public void accept(VTreeVisitor visitor) {
        visitor.visitComponent(this);
    }


    public VTree ensureRendered() {
        if(vNode == null) {
            VDomLogger.event(this, "willMount");
            componentWillMount();

            vNode = render();
            assert vNode != null;
        }
        return vNode;
    }

    public VTree forceRender() {
        VDomLogger.event(this, "forceRender");

        if(!isRendered()) {
            VDomLogger.event(this, "willMount");
            componentWillMount();
        }
        vNode = render();
        dirty = false;
        return vNode;
    }

    public String getPropertiesForDebugging() {
        return "";
    }

    public String getDebugId() {
        return getClass().getSimpleName() + "#" + debugIndex + "[ " + getPropertiesForDebugging() + " ]";
    }

    @Override
    public String toString() {
        return getDebugId();
    }

    public int getDebugIndex() {
        return debugIndex;
    }
}
