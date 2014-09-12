package org.activityinfo.ui.vdom.shared.dom;

import java.util.HashMap;
import java.util.Map;

public class TestElement extends TestNode implements DomElement {

    private String tagName;
    private Map<String, String> attributes = new HashMap<>();
    private Map<String, String> properties = new HashMap<>();
    private Map<String, String> style = new HashMap<>();

    public TestElement(String tagName) {
        this.tagName = tagName.toLowerCase();
    }

    @Override
    public void removeAttribute(String attrName) {
        this.attributes.remove(attrName);
    }

    @Override
    public void setPropertyString(String propName, String propValue) {
        this.properties.put(propName, propValue);
    }

    @Override
    public void clearStyleProperty(String name) {
        this.style.remove(name);
    }

    @Override
    public void setAttribute(String key, String value) {
        this.attributes.put(key, value);
    }

    @Override
    public void setStyleProperty(String key, String value) {
        this.style.put(key, value);
    }

    @Override
    public int getChildCount() {
        return children.size();
    }

    @Override
    public String getTagName() {
        return tagName;
    }

    @Override
    public String getInputValue() {
        return null;
    }

    @Override
    public int getNodeType() {
        return ELEMENT_NODE;
    }

    @Override
    public String toString() {
        if(children.isEmpty()) {
            return "<" + tagName + "></"+ tagName + ">";
        } else {
            StringBuilder html = new StringBuilder();
            html.append("<" + tagName + ">");
            for(DomNode node : children) {
                html.append(node);
            }
            html.append("</" + tagName + ">");
            return html.toString();
        }
    }

    @Override
    public void writeTo(StringBuilder html, String indent) {

        html.append(indent).append("<").append(tagName);

        if(properties.containsKey("className")) {
            html.append(" class=\"").append(properties.get("className")).append("\"");
        }

        html.append(">");

        if(children.size() == 1 && children.get(0) instanceof TestTextNode) {
            html.append(((TestTextNode) children.get(0)).getData());

        } else if(children.size() >= 1) {
            html.append("\n");
            for (TestNode node : children) {
                node.writeTo(html, indent + "  ");
            }
            html.append(indent);
        }

        html.append("</").append(tagName).append(">").append("\n");
    }
}
