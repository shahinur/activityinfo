package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;

/**
 * Constructors for form elements using Bootstrap styling
 */
public class Forms {

    public enum InputType {
        TEXT
    }

    public static VNode input(InputType type, String name, String placeholder) {
        PropMap propMap = new PropMap()
                .set("type", type.name().toLowerCase())
                .set("className", BaseStyles.FORM_CONTROL.getClassNames())
                .set("name", name)
                .set("placeholder", placeholder);

        return new VNode(HtmlTag.INPUT, propMap);
    }
}
