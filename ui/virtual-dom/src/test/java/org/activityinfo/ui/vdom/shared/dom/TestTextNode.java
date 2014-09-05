package org.activityinfo.ui.vdom.shared.dom;

public class TestTextNode extends TestNode implements DomText {

    private String text;

    public TestTextNode(String text) {
        this.text = text;
    }

    @Override
    public void setData(String text) {
        this.text = text;
    }

    @Override
    public String getData() {
        return text;
    }

    @Override
    public int getNodeType() {
        return DomNode.TEXT_NODE;
    }

    @Override
    public String toString() {
        return text;
    }
}
