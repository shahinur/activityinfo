package org.activityinfo.ui.vdom.shared.dom;

import java.util.ArrayList;
import java.util.List;

public abstract class TestNode implements DomNode {

    TestNode parent;

    protected final List<TestNode> children = new ArrayList<>();

    @Override
    public void removeAllChildren() {
        children.clear();
    }

    @Override
    public DomNode getChildDomNode(int i) {
        return children.get(i);
    }

    @Override
    public DomNode getParentNode() {
        return parent;
    }

    @Override
    public void removeChild(DomNode domNode) {
        assert domNode instanceof TestNode;
        children.remove(domNode);
    }

    @Override
    public void appendChild(DomNode domNode) {
        assert domNode instanceof TestNode;
        children.add((TestNode) domNode);
    }

    @Override
    public void replaceChild(DomNode newNode, DomNode oldNode) {
        assert newNode instanceof TestNode;
        assert oldNode instanceof TestNode;

        int index = children.indexOf(oldNode);
        assert index != -1 : "cannot find oldNode";
        children.set(index, (TestNode)oldNode);
    }
}
