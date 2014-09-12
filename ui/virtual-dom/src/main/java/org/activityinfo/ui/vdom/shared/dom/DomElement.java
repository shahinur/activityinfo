package org.activityinfo.ui.vdom.shared.dom;

import com.google.gwt.core.client.SingleJsoImpl;

/**
 * Interface to a DOM Document that decouples the
 * virtual dom rendering and diffing mechanism from GWTs JSNI objects,
 * which complicate testing.
 *
 * @see com.google.gwt.dom.client.Element
 */
@SingleJsoImpl(BrowserDomElement.class)
public interface DomElement extends DomNode {

    void removeAttribute(String attrName);

    void setPropertyString(String propName, String propValue);

    void clearStyleProperty(String name);

    void setAttribute(String key, String value);

    void setStyleProperty(String key, String value);

    void appendChild(DomNode domNode);

    int getChildCount();

    String getTagName();

    String getInputValue();
}
