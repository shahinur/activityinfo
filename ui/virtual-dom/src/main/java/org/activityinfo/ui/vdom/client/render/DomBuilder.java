package org.activityinfo.ui.vdom.client.render;

import org.activityinfo.ui.vdom.shared.dom.DomDocument;
import org.activityinfo.ui.vdom.shared.dom.DomElement;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.dom.DomText;
import org.activityinfo.ui.vdom.shared.tree.*;

/**
 * Builds a DOM element tree from virtual dom tree
 */
public class DomBuilder {

    private RenderContext context;
    private DomDocument doc;


    public DomBuilder(RenderContext context) {
        this.context = context;
        this.doc = context.getDocument();
    }

    public void updateRoot(DomElement rootElement, VTree vtree) {
        if(vtree instanceof VNode) {
            updateRootNode(rootElement, (VNode) vtree);
        } else if(vtree instanceof VComponent) {
            updateRootThunk(rootElement, (VComponent)vtree);
        } else {
            throw new IllegalStateException("Root vTree must be an element");
        }
    }

    public void updateRootNode(DomElement rootElement, VNode vNode) {
        if(!rootElement.getTagName().equalsIgnoreCase(vNode.tag.name())) {
            throw new UnsupportedOperationException("Cannot change the tag name of the root element");
        }
        Properties.applyProperties(rootElement, vNode.properties, null);

        rootElement.removeAllChildren();

        appendChildren(rootElement, vNode);
    }


    public DomNode render(VTree vTree) {

        if(vTree instanceof VComponent) {
            return renderComponent((VComponent) vTree);

        } else if(vTree instanceof VText) {
            return renderText((VText) vTree);

        } else if(vTree instanceof VNode) {
            return renderTree((VNode) vTree);

        } else {
            throw new IllegalArgumentException("Unknown virtual node " + vTree);
        }
    }

    private DomElement renderTree(VNode vnode) {
        DomElement domElement;
        if(vnode.namespace == null) {
            domElement = doc.createElement(vnode.tag);
        } else {
            throw new UnsupportedOperationException("Todo: namespaces");
        }

        PropMap props = vnode.properties;
        if(props != null) {
            Properties.applyProperties(domElement, props, null);
        }

        return appendChildren(domElement, vnode);
    }

    private DomElement appendChildren(DomElement domElement, VNode vnode) {
        VTree[] children = vnode.children;
        for (int i = 0; i < children.length; ++i) {
            domElement.appendChild(render(children[i]));
        }

        return domElement;
    }

    private DomNode renderComponent(VComponent thunk) {
        VTree virtualNode = thunk.forceRender();
        DomNode domNode = render(virtualNode);

        if(thunk.getEventMask() > 0) {
            context.registerEventListener(domNode, thunk);
        }

        thunk.fireMounted(context, domNode);

        return domNode;
    }

    private void updateRootThunk(DomElement rootElement, VComponent thunk) {
        VTree tree = materializeThunk(thunk);
        updateRoot(rootElement, tree);

        thunk.fireMounted(context, rootElement);
    }

    private VTree materializeThunk(VComponent thunk) {
        return thunk.force();
    }

    private DomText renderText(VText vTree) {
        return doc.createTextNode(vTree.getText());
    }

}
