package org.activityinfo.ui.vdom.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.client.dom.BrowserDomDocument;
import org.activityinfo.ui.vdom.client.dom.BrowserDomElement;
import org.activityinfo.ui.vdom.client.dom.BrowserDomNode;
import org.activityinfo.ui.vdom.client.render.DomBuilder;
import org.activityinfo.ui.vdom.client.render.DomPatcher;
import org.activityinfo.ui.vdom.client.render.RenderContext;
import org.activityinfo.ui.vdom.shared.diff.Diff;
import org.activityinfo.ui.vdom.shared.diff.VDiff;
import org.activityinfo.ui.vdom.shared.dom.DomDocument;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.ArrayList;
import java.util.List;
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
        setElement((Element)domBuilder.render(vNode));
        completeWidgetAdoptions();
        completeDetachments();
        tree = vNode;
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
            completeWidgetAdoptions();
            completeDetachments();
        }
    }

    private void renderInitial(VTree vNode) {
        domBuilder.updateRoot(BrowserDomElement.cast(getElement()), vNode);
        tree = vNode;
    }

    private void patchTree(VTree newTree) {
        VDiff diff = Diff.diff(tree, newTree);

        DomPatcher domPatcher = new DomPatcher(domBuilder, this);
        DomNode rootNode = domPatcher.patch(getRootNode(), diff);

        if(rootNode != getElement()) {
            throw new IllegalStateException("Cannot replace the root node!");
        }

        tree = newTree;

    }

    private BrowserDomNode getRootNode() {
        return getElement().<BrowserDomNode>cast();
    }

    @Override
    public DomDocument getDocument() {
        return document;
    }

    @Override
    public void attachWidget(Widget child) {
        // Detach new child.
        child.removeFromParent();

        // Logical attach.
        getChildren().add(child);

        // wait until we're done updating to complete the
        // adoption process because the node may not yet be added
        // to the tree
        pendingAttachments.add(child);
    }

    @Override
    public void detachWidget(Element element) {
        Widget widget = this.findWidget(element);
        if(widget == null) {
            LOGGER.log(Level.WARNING, "Could not find widget for element " + element);
        } else {
            try {
                orphan(widget);
                pendingDetachments.add(widget);
            } catch (Throwable caught) {
                LOGGER.log(Level.SEVERE, "Exception while detaching widget", caught);
            }
        }
    }

    private Widget findWidget(Element element) {
        for(Widget child : getChildren()) {
            if(child.getElement() == element) {
                return child;
            }
        }
        return null;
    }

    @Override
    public void fireUpdate(VThunk thunk) {

    }

    private void completeWidgetAdoptions() {
        for(Widget child : pendingAttachments) {
            try {
                adopt(child);
            } catch(Exception e) {
                LOGGER.log(Level.SEVERE, "Exception while completing adoption of " + child, e);
            }
        }
        pendingAttachments.clear();
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
}
