package org.activityinfo.ui.vdom.shared.dom;

import java.util.ArrayList;
import java.util.List;

public abstract class TestNode implements DomNode {

    TestNode parent;

    protected final List<TestNode> children = new ArrayList<>();

    @Override
    public void removeAllChildren() {
        for(TestNode node : children) {
            node.parent = null;
        }
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
        ((TestNode) domNode).parent = null;
        children.remove(domNode);
    }

    @Override
    public void appendChild(DomNode domNode) {
        assert domNode instanceof TestNode;
        TestNode testNode = (TestNode)domNode;
        assert testNode.parent == null : "node is already attached";

        children.add(testNode);
        testNode.parent = this;
    }

    @Override
    public void replaceChild(DomNode newNode, DomNode oldNode) {
        assert newNode instanceof TestNode;
        assert oldNode instanceof TestNode;
        TestNode newTestNode = (TestNode) newNode;
        TestNode oldTestNode = (TestNode) oldNode;

        assert newTestNode.parent == null : newNode + " is already attached";

        int index = children.indexOf(oldTestNode);
        assert index != -1 : "cannot find oldNode";
        children.set(index, newTestNode);

        oldTestNode.parent = null;
        newTestNode.parent = this;
    }

    public abstract void writeTo(StringBuilder html, String indent);
}
