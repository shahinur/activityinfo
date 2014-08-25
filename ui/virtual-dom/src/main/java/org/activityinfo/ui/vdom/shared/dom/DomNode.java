package org.activityinfo.ui.vdom.shared.dom;

public interface DomNode {

    short ELEMENT_NODE = 1;

    /**
     * The constant 3 denotes DOM nodes of type Text.
     */
    short TEXT_NODE = 3;

    void removeAllChildren();

    DomNode getChildDomNode(int i);

    DomNode getParentNode();

    int getNodeType();

    void removeChild(DomNode domNode);

    void appendChild(DomNode domNode);

    void replaceChild(DomNode newNode, DomNode oldNode);

}
