package org.activityinfo.ui.app.client.chrome.side;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Event;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.MENUTOGGLE;

/**
 * Button that toggles the side navigation bar.
 */
public class SidebarToggleButton extends VComponent {

    private boolean collapsed = false;

    @Override
    protected VTree render() {
        return new VNode(HtmlTag.A, PropMap.withClasses(MENUTOGGLE), FontAwesome.BARS.render());
    }

    @Override
    public int getEventMask() {
        return Event.ONCLICK;
    }

    @Override
    public void onBrowserEvent(DomEvent event) {
        if(event.getTypeInt() == Event.ONCLICK) {
            collapsed = !collapsed;
            if(collapsed) {
                Document.get().getBody().addClassName(BaseStyles.LEFTPANEL_COLLAPSED.getClassNames());
            } else {
                Document.get().getBody().removeClassName(BaseStyles.LEFTPANEL_COLLAPSED.getClassNames());
            }
        }
    }
}
