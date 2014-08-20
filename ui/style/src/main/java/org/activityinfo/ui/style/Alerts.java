package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.AriaRole;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.className;
import static org.activityinfo.ui.vdom.shared.html.H.div;

/**
 * Provide contextual feedback messages for typical user actions with the handful of
 * available and flexible alert messages.
 *
 * @see <a href="http://getbootstrap.com/components/#alerts">Bootstrap Docs</a>
 */
public class Alerts {

    public static VNode alert(AlertStyle style, VTree... children) {
        return div(className(style).role(AriaRole.ALERT), children);
    }
}
