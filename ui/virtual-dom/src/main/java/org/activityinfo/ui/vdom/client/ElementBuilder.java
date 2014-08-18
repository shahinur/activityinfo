package org.activityinfo.ui.vdom.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import org.activityinfo.ui.vdom.shared.tree.*;

import static org.activityinfo.ui.vdom.shared.tree.VNode.isVNode;

/**
 * Builds a DOM element from virtual dom tree
 */
public class ElementBuilder {

    private Document doc = Document.get();
    private boolean warn = true;

    public Node createElement(VTree vTree) {
        vTree = Thunks.handleThunk(vTree).a;

        if (vTree instanceof VWidget) {
            return ((VWidget) vTree).init();

        } else if (vTree instanceof VText) {
            return doc.createTextNode(((VText) vTree).text);

        } else if (!isVNode(vTree)) {
            if (warn) {
                GWT.log("Item is not a valid virtual dom node: " + vTree);
            }
            return null;
        }

        VNode vnode = (VNode) vTree;

        Node node;
        if(vnode.namespace == null) {
            node = doc.createElement(vnode.tag.name().toLowerCase());
        } else {
            throw new UnsupportedOperationException("NS with GWT?");
            //node = doc.createElement(vnode.namespace, vnode.tagName);
        }

        PropMap props = vnode.properties;
        Properties.applyProperties((Element)node, props, null);

        VTree[] children = vnode.children;
        for(int i=0;i<children.length;++i) {
            Node childNode = createElement(children[i]);
            if(childNode != null) {
                node.appendChild(childNode);
            }
        }

        return node;
    }
}
