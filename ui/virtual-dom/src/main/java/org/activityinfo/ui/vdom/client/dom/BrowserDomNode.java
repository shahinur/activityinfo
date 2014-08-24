package org.activityinfo.ui.vdom.client.dom;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Node;
import org.activityinfo.ui.vdom.shared.dom.DomNode;

public class BrowserDomNode extends JavaScriptObject implements DomNode {

    protected BrowserDomNode() {
    }

    private final Node asNode() {
        return this.cast();
    }

    @Override
    public final void removeAllChildren() {
        asNode().removeAllChildren();
    }

    @Override
    public final DomNode getChildDomNode(int i) {
        return this.asNode().getChild(i).<BrowserDomNode>cast();
    }

    @Override
    public final DomNode getParentNode() {
        return asNode().getParentNode().<BrowserDomNode>cast();
    }

    @Override
    public final int getNodeType() {
        return asNode().getNodeType();
    }

    @Override
    public final void removeChild(DomNode domNode) {
        asNode().removeChild((Node) domNode);
    }

    @Override
    public final void appendChild(DomNode domNode) {
        asNode().appendChild((Node) domNode);
    }

    @Override
    public final void replaceChild(DomNode newNode, DomNode oldNode) {
        asNode().replaceChild((Node) newNode, (Node) oldNode);
    }

    public static DomNode cast(Node element) {
        return element.<BrowserDomNode>cast();
    }
}
