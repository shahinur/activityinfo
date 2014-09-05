package org.activityinfo.ui.vdom.shared.dom;

import org.activityinfo.ui.vdom.shared.tree.Tag;

public class TestDocument implements DomDocument {
    @Override
    public DomElement createElement(Tag tagName) {
        return new TestElement(tagName.name());
    }

    @Override
    public DomText createTextNode(String text) {
        return new TestTextNode(text);
    }
}
