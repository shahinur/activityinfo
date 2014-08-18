package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VText;

public class Badges {

    public static VNode badge(int count) {
        return badge(Integer.toString(count));
    }

    private static VNode badge(String text) {
        return new VNode(HtmlTag.SPAN, PropMap.withClasses(BaseStyles.BADGE), new VText(text));
    }
}
