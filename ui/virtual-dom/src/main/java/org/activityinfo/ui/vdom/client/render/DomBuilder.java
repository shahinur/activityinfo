package org.activityinfo.ui.vdom.client.render;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Text;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.vdom.shared.tree.*;

/**
 * Builds a DOM element tree from virtual dom tree
 */
public class DomBuilder {

    private RenderContext context;
    private Document doc = Document.get();

    public DomBuilder(RenderContext context, Document doc) {
        this.context = context;
        this.doc = doc;
    }

    public void updateRoot(Element rootElement, VTree vtree) {
        if(vtree instanceof VNode) {
            updateRoot(rootElement, (VNode) vtree);
        } else if(vtree instanceof VThunk) {
            updateRootThunk(rootElement, (VThunk)vtree);
        } else {
            throw new IllegalStateException("Root vTree must be an element");
        }
    }

    public void updateRoot(Element rootElement, VNode vNode) {
        if(!rootElement.getTagName().equalsIgnoreCase(vNode.tag.name())) {
            throw new UnsupportedOperationException("Cannot change the tag name of the root element");
        }
        Properties.applyProperties(rootElement, vNode.properties, null);

        rootElement.removeAllChildren();

        appendChildren(rootElement, vNode);
    }


    public Node render(VTree vTree) {

        if(vTree instanceof VThunk) {
            return renderThunk((VThunk) vTree);

        } else if(vTree instanceof VWidget) {
            return renderWidget((VWidget) vTree);

        } else if(vTree instanceof VText) {
            return renderText((VText) vTree);

        } else if(vTree instanceof VNode) {
            return renderTree((VNode) vTree);

        } else {
            throw new IllegalArgumentException("Unknown virtual node " + vTree);
        }
    }

    private Node renderTree(VNode vnode) {
        Element domElement;
        if(vnode.namespace == null) {
            domElement = doc.createElement(vnode.tag.name().toLowerCase());
        } else {
            throw new UnsupportedOperationException("Todo: namespaces");
        }

        PropMap props = vnode.properties;
        if(props != null) {
            Properties.applyProperties(domElement, props, null);
        }

        return appendChildren(domElement, vnode);
    }

    private Node appendChildren(Element domElement, VNode vnode) {
        VTree[] children = vnode.children;
        for (int i = 0; i < children.length; ++i) {
            domElement.appendChild(render(children[i]));
        }

        return domElement;
    }

    private Node renderThunk(VThunk thunk) {
        VTree virtualNode = materializeThunk(thunk);
        Node domNode = render(virtualNode);

        thunk.onMounted();

        return domNode;
    }

    private void updateRootThunk(Element rootElement, VThunk thunk) {
        VTree tree = materializeThunk(thunk);
        updateRoot(rootElement, tree);

        thunk.onMounted();
    }

    private VTree materializeThunk(VThunk thunk) {
        return thunk.force();
    }

    private Text renderText(VText vTree) {
        return doc.createTextNode(vTree.text);
    }

    private Node renderWidget(VWidget vWidget) {
        Widget widget = vWidget.createWidget().asWidget();
        context.attachWidget(widget);

        return widget.getElement();
    }
}
