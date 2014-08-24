package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.AriaRole;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

/**
 * Provide contextual feedback messages for typical user actions with the handful of
 * available and flexible alert messages.
 *
 * @see <a href="http://getbootstrap.com/components/#alerts">Bootstrap Docs</a>
 */
public class Alert extends VThunk {

    private AlertStyle style;
    private VTree[] content;

    public Alert(AlertStyle style, VTree... content) {
        this.style = style;
        this.content = content;
    }

    @Override
    protected VTree render(VThunk previous) {
        return new VNode(HtmlTag.DIV, PropMap.withClasses(style.getClassNames()).role(AriaRole.ALERT), content);
    }
}
