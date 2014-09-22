package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.CssClass;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;

import static org.activityinfo.ui.vdom.shared.html.H.t;

/**
 * Constructors for form elements using Bootstrap styling
 */
public class Forms {

    public static VNode input(InputControlType type, String name, String placeholder) {
        PropMap propMap = new PropMap()
                .set("type", type.name().toLowerCase())
                .set("className", BaseStyles.FORM_CONTROL.getClassNames())
                .set("name", name)
                .set("placeholder", placeholder);

        return new VNode(HtmlTag.INPUT, propMap);
    }

    public static VNode label(String text) {
        return label(BaseStyles.COL_SM_3, text);
    }

    public static VNode label(CssClass cssClass,  String text) {
        return label(PropMap.withClasses(cssClass), text);
    }

    public static VNode label(PropMap propMap,  String text) {
        return H.label(propMap, t(text));
    }
}
