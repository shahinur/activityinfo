package org.activityinfo.ui.vdom.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.client.render.DomBuilder;
import org.activityinfo.ui.vdom.client.render.DomPatcher;
import org.activityinfo.ui.vdom.client.render.RenderContext;
import org.activityinfo.ui.vdom.shared.diff.Diff;
import org.activityinfo.ui.vdom.shared.diff.VPatchSet;
import org.activityinfo.ui.vdom.shared.dom.*;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A GWT Widget that hosts a {@link org.activityinfo.ui.vdom.shared.tree.VTree}
 */
public class VDomWidget extends ComplexPanel implements RenderContext {

    private static final Logger LOGGER = Logger.getLogger(VDomWidget.class.getName());

    private BrowserDomDocument document = new BrowserDomDocument();

    private VTree tree = null;

    private boolean updating = false;

    private Map<DomNode, VComponent> componentMap = new HashMap<>();

    private boolean updateQueued = false;

    /**
     * Widgets that have been created as part of rendering a new or updated
     * tree but may not yet have been physically attached to the DOM tree.
     */
    private List<Widget> pendingAttachments = new ArrayList<>();
    private List<Widget> pendingDetachments = new ArrayList<>();

    private DomBuilder domBuilder;

    public VDomWidget() {
        domBuilder = new DomBuilder(this);
        setElement(Document.get().createDivElement());
    }

    public VDomWidget(VNode vNode) {
        domBuilder = new DomBuilder(this);
        setElement((Element) domBuilder.render(vNode));
        completeDetachments();
        tree = vNode;
        sinkEvents(Event.ONCLICK | Event.FOCUSEVENTS | Event.ONCHANGE);
    }

    public void update(VTree vTree) {
        assert !updating : "Update already in progress";
        try {
            updating = true;
            if (tree == null) {
                renderInitial(vTree);
            } else {
                patchTree(vTree);
            }
        } finally {
            updating = false;
            completeDetachments();
        }
    }

    private void renderInitial(VTree vNode) {
        domBuilder.updateRoot(BrowserDomElement.cast(getElement()), vNode);
        tree = vNode;
    }

    /**
     * Re-renders the entire tree (with the exception of thunks) and
     * applies the differences to the DOM tree
     *
     * @param newTree
     */
    private void patchTree(VTree newTree) {
        VPatchSet diff = Diff.diff(tree, newTree);

        patch(diff);

        tree = newTree;
    }

    /**
     * Re-renders and diffs only those VThunks explicitly marked as dirty
     * during the last event loop
     */
    private void patchDirty() {
        patch(Diff.diff(tree, tree));
    }


    private void patch(VPatchSet diff) {
        DomPatcher domPatcher = new DomPatcher(domBuilder, this);
        DomNode rootNode = domPatcher.patch(getRootNode(), diff);

        if(rootNode != getElement()) {
            throw new IllegalStateException("Cannot replace the root node!");
        }

        cleanUpEventListeners();
    }


    private BrowserDomNode getRootNode() {
        return getElement().<BrowserDomNode>cast();
    }

    @Override
    public DomDocument getDocument() {
        return document;
    }

    @Override
    public void attachWidget(Widget child, DomElement container) {
        this.add(child, BrowserDomElement.cast(container));
    }

    @Override
    public void detachWidget(Widget child) {
        pendingDetachments.add(child);
    }

    @Override
    public void fireUpdate(VComponent thunk) {
        if(!updateQueued) {
            updateQueued = false;
            Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    patchDirty();
                }
            });
        }
    }


    @Override
    public void registerEventListener(VComponent thunk, DomNode node) {
        componentMap.put(node, thunk);
//        sinkEvents(thunk.getEventMask());
    }


    @Override
    public void onBrowserEvent(Event event) {
        Element domNode = event.getEventTarget().cast();
        while(true) {
            if (domNode == getElement()) {
                break;
            }
            VComponent component = componentMap.get(domNode);
            if(component != null) {
                component.onBrowserEvent(BrowserDomEvent.cast(event));
            }
            domNode = domNode.getParentElement();
        }
    }

    @Override
    public void componentUnmounted(VComponent component, DomNode domNode) {
        componentMap.remove(domNode);
    }

    private void completeDetachments() {
        for(Widget widget : pendingDetachments) {
            try {
                getChildren().remove(widget);
            } catch(Exception e) {
                LOGGER.log(Level.SEVERE, "Exception while completing removal of " + widget, e);
            }
        }
        pendingDetachments.clear();
    }

    private void cleanUpEventListeners() {
        Element container = getElement();
        Iterator<Map.Entry<DomNode, VComponent>> it = componentMap.entrySet().iterator();
        while(it.hasNext()) {
            Element element = ((BrowserDomNode)it.next().getKey()).cast();
            if(!container.isOrHasChild(element)) {
                it.remove();
            }
        }
    }
}
