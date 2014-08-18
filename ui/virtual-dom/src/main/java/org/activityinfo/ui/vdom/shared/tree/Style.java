package org.activityinfo.ui.vdom.shared.tree;

public class Style {

    private PropMap declarations = new PropMap();

    public Style textAlign(String align) {
        return set("textAlign", "center");
    }

    public Style verticalAlign(String align) {
        return set("verticalAlign", "center");
    }

    public Style lineHeight(int height) {
        return setPixels("lineHeight", height);
    }

    public Style border(String border) {
        return set("border", border);
    }

    public Style width(int width) {
        return setPixels("width", width);
    }

    public Style height(int height) {
        return setPixels("height", height);
    }

    private Style setPixels(String propName, int pixels) {
        return set(propName, pixels + "px");
    }

    private Style set(String propName, String value) {
        declarations.set(propName, value);
        return this;
    }

    public PropMap asPropMap() {
        return new PropMap().set("style", declarations);
    }

    /**
     * Append the overflow CSS property.
     */
    public Style overflow(com.google.gwt.dom.client.Style.Overflow value) {
        return set("overflow", value.getCssName());
    }



}
