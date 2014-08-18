package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class Button {

    public static VNode button(ButtonStyle style, String text) {
        return button(className(style), t(text));
    }

    public static VNode button(ButtonStyle style, ButtonSize size, String text) {
        return button(classNames(style, size), t(text));
    }

    public static VNode dropDownToggle(VTree icon, VTree label) {
        return button(classNames(ButtonStyle.DEFAULT, BaseStyles.DROPDOWN_TOGGLE),
                  icon, label, caret());
    }


    public static VNode caret() {
        return new VNode(HtmlTag.SPAN, className(BaseStyles.CARET));
    }

    private static VNode button(PropMap propMap, VTree... children) {
        return new VNode(HtmlTag.BUTTON, propMap.set("type", "button"), children);
    }

}
