package org.activityinfo.ui.vdom.client;

import com.google.common.base.Preconditions;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.client.render.DomBuilder;
import org.activityinfo.ui.vdom.client.render.DomPatcher;
import org.activityinfo.ui.vdom.client.render.RenderContext;
import org.activityinfo.ui.vdom.shared.diff.Diff;
import org.activityinfo.ui.vdom.shared.diff.VDiff;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VDomWidget extends ComplexPanel implements RenderContext {

    private static final Logger LOGGER = Logger.getLogger(VDomWidget.class.getName());

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
        domBuilder = new DomBuilder(this, Document.get());
        setElement(Document.get().createDivElement());
    }

    public VDomWidget(VNode vNode) {
        domBuilder = new DomBuilder(this, Document.get());
        setElement(domBuilder.render(vNode).<Element>cast());
        completeWidgetAdoptions();
        completeDetachments();
        tree = vNode;
    }

    public void update(VTree vTree) {
        Preconditions.checkState(!updating, "Update already in progress");
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
        domBuilder.updateRoot(getElement(), vNode);
        tree = vNode;
    }

    private void patchTree(VTree newTree) {
        VDiff diff = Diff.diff(tree, newTree);

        DomPatcher domPatcher = new DomPatcher(domBuilder, this);
        Node rootNode = domPatcher.patch(getElement(), diff);

        if(rootNode != getElement()) {
            throw new IllegalStateException("Cannot replace the root node!");
        }

        tree = newTree;

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
