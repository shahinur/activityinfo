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

    public Style border(int border) {
        return setPixels("border", border);
    }

    public Style border(String shorthand) {
        return set("border", shorthand);
    }

    public Style width(int width) {
        return setPixels("width", width);
    }

    public Style height(int height) {
        return setPixels("height", height);
    }

    public Style display(com.google.gwt.dom.client.Style.Display display) {
        return set("display", display.getCssName());
    }

    private Style setPixels(String propName, int pixels) {
        if(pixels == 0) {
            return set(propName, Integer.toString(pixels));
        } else {
            return set(propName, pixels + "px");
        }
    }

    public Style fontSize(int size) { return setPixels("fontSize", size); }


    public Style set(String propName, String value) {
        declarations.set(propName, value);
        return this;
    }

    public Style setPosition(com.google.gwt.dom.client.Style.Position position) {
        return set("position", position.getCssName());
    }

    public PropMap asPropMap() {
        return declarations;
    }

    /**
     * Append the overflow CSS property.
     */
    public Style overflow(com.google.gwt.dom.client.Style.Overflow value) {
        return set("overflow", value.getCssName());
    }


}
