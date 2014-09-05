package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.className;

public class Buttons {
    public static VNode dropDownToggle(VTree icon, VTree label) {
        return new VNode(HtmlTag.BUTTON, PropMap.withClasses(ButtonStyle.DEFAULT.getClassNames() +
            " " + BaseStyles.DROPDOWN_TOGGLE.getClassNames()),
                  new VTree[] { icon, label, caret() });
    }

    public static VNode caret() {
        return new VNode(HtmlTag.SPAN, className(BaseStyles.CARET));
    }
}
