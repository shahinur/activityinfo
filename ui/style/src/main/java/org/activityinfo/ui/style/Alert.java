package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.AriaRole;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.*;

import static com.google.gwt.dom.client.Style.Display.BLOCK;
import static com.google.gwt.dom.client.Style.Display.NONE;

/**
 * Provide contextual feedback messages for typical user actions with the handful of
 * available and flexible alert messages.
 *
 * @see <a href="http://getbootstrap.com/components/#alerts">Bootstrap Docs</a>
 */
public class Alert extends VComponent {

    private AlertStyle style;
    private VTree[] content;
    private boolean visible = true;

    public Alert(AlertStyle style, VTree... content) {
        this.style = style;
        this.content = content;
    }

    @Override
    protected VTree render() {
        PropMap props = PropMap.withClasses(style.getClassNames()).role(AriaRole.ALERT);
        if (visible) {
            props.setStyle(new Style().display(BLOCK));
        } else {
            props.setStyle(new Style().display(NONE));
        }

        return new VNode(HtmlTag.DIV, props, content);
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            refresh();
        }
    }

    @Override
    public String getPropertiesForDebugging() {
        return "content = " + content;
    }
}
