package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VText;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class Button {

    /**
     * Creates a new button with the given style and text
     *
     */
    public static VNode button(ButtonStyle style, String text) {
        return button(className(style), t(text));
    }

    public static VNode button(ButtonStyle style, ButtonSize size, String text) {
        return button(classNames(style, size), t(text));
    }

    public static VNode button(String text) {
        return button(className(ButtonStyle.DEFAULT), new VText(text));
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

    public static VNode close() {
        //<button aria-hidden="true" data-dismiss="alert" class="close" type="button">×</button>
        return button(className(BaseStyles.CLOSE).ariaHidden().data("dismiss", "alert"));
    }

}
