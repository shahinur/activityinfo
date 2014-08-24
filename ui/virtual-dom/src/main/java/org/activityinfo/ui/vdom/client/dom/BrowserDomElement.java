package org.activityinfo.ui.vdom.client.dom;

import com.google.gwt.dom.client.Element;
import org.activityinfo.ui.vdom.shared.dom.DomElement;

public class BrowserDomElement extends BrowserDomNode implements DomElement {

    protected BrowserDomElement() {}

    @Override
    public final void removeAttribute(String attrName) {
        asElement().removeAttribute(attrName);
    }

    @Override
    public final void setPropertyString(String propName, String propValue) {
        asElement().setPropertyString(propName, propValue);
    }

    @Override
    public final void clearStyleProperty(String name) {
        asElement().getStyle().clearProperty(name);
    }

    @Override
    public final void setAttribute(String key, String value) {
        asElement().setAttribute(key, value);
    }

    private Element asElement() {
        return this.cast();
    }

    @Override
    public final void setStyleProperty(String key, String value) {
        asElement().getStyle().setProperty(key, value);
    }

    @Override
    public final String getTagName() {
        return asElement().getTagName();
    }

    public static BrowserDomElement cast(Element element) {
        return element.cast();
    }
}
