package org.activityinfo.ui.vdom.shared.html;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import org.activityinfo.ui.vdom.shared.tree.*;

public class HtmlRenderer implements VTreeVisitor {

    public static final String QUOTE = "\"";

    private StringBuilder html;
    private boolean prettyPrint;
    private int currentIndentLevel;

    public HtmlRenderer() {
        html = new StringBuilder();
    }

    public void visitNode(VNode node) {
        String tagName = node.tag.name().toLowerCase();
        html.append("<").append(tagName);

        appendProperties(node);
        html.append(">");

        if(node.tag.isSingleton()) {
            // Tags like <input> and <br> are not closed...
            // but they also don't have children
            assert node.children == null;

        } else {

            appendChildren(node.children);
            html.append("</").append(tagName).append("/>");
        }
    }

    private void appendProperties(VNode node) {
        if(node.properties != null) {
            for(String propName : node.properties.keys()) {
                Object propValue = node.properties.get(propName);
                switch (propName) {
                    case "className":
                        appendProperty("class", (String) propValue);
                        break;

                    case "style":
                        appendStyleProperty((PropMap) propValue);
                        break;

                    default:
                        appendProperty(propName, (String) propValue);
                        break;
                }
            }
        }
    }

    private void appendChildren(VTree[] children) {
        if(children != null) {
            for(int i=0;i!=children.length;++i) {
                children[i].accept(this);
            }
        }
    }

    private void appendProperty(String attributeName, String value) {
        html.append(" ")
            .append(attributeName)
                .append("=")
                .append(QUOTE)
                .append(SafeHtmlUtils.htmlEscape(value))
                .append(QUOTE);
    }

    private void appendStyleProperty(PropMap styleMap) {
        // NOTE: Styles are assumed to be NOT user provided
        // and so were are not escaping/checking. is that right?
        if(!styleMap.isEmpty()) {
            html.append(" style=\"");
            for(String name : styleMap.keys()) {
                String value = (String) styleMap.get(name);
                html.append(name).append(":").append(value).append(";");
            }
            html.append(QUOTE);
        }
    }
    
    @Override
    public void visitText(VText text) {
        html.append(SafeHtmlUtils.htmlEscape(text.text));
    }

    @Override
    public void visitThunk(VThunk vThunk) {
        vThunk.render(null).accept(this);
    }

    @Override
    public void visitWidget(VWidget widget) {
        // noop??
    }

    public String getHtml() {
        return html.toString();
    }
}
