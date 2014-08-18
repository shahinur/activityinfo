package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;

/**
 * Constructors for form elements using Boostrap styling
 */
public class Forms {

    public enum ButtonStyle {
        DEFAULT
    }

    public static VNode textInput(String name, String placeholder) {
        PropMap propMap = new PropMap()
                .set("type", "text")
                .set("className", "form-control")
                .set("name", "keyword")
                .set("placeholder", "Search here...");

        return new VNode(HtmlTag.INPUT, propMap);
    }
}
