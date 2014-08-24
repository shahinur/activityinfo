package org.activityinfo.ui.vdom.shared.dom;

public interface DomElement extends DomNode {

    void removeAttribute(String attrName);

    void setPropertyString(String propName, String propValue);

    void clearStyleProperty(String name);

    void setAttribute(String key, String value);

    void setStyleProperty(String key, String value);

    void appendChild(DomNode domNode);

    String getTagName();

}
