package org.activityinfo.ui.vdom.shared.dom;

import org.activityinfo.ui.vdom.shared.tree.Tag;

/**
 * Interface to a DOM Document that decouples the
 * virtual dom rendering and diffing mechanism from GWTs JSNI objects,
 * which complicate testing.
 *
 * @see com.google.gwt.dom.client.Document
 */
public interface DomDocument {


    DomElement createElement(Tag tagName);

    DomText createTextNode(String text);
}
