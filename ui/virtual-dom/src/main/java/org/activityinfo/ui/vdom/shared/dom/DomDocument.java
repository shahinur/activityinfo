package org.activityinfo.ui.vdom.shared.dom;

import org.activityinfo.ui.vdom.shared.tree.Tag;

public interface DomDocument {

    DomElement createElement(Tag tagName);

    DomText createTextNode(String text);
}
