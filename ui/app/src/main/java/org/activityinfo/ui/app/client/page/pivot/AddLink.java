package org.activityinfo.ui.app.client.page.pivot;

import com.google.gwt.user.client.Event;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.a;
import static org.activityinfo.ui.vdom.shared.html.H.t;

public class AddLink extends VComponent {

    private final String label;
    private ClickHandler clickHandler;

    public AddLink(String label, ClickHandler clickHandler) {
        this.label = label;
        this.clickHandler = clickHandler;
    }

    @Override
    protected VTree render() {
        PropMap propMap = new PropMap();
        propMap.setClass(BaseStyles.PULL_RIGHT);
        propMap.set("href", "#");

        return a(propMap, t("+ "), t(label));
    }

    @Override
    public int getEventMask() {
        return Event.ONCLICK;
    }

    @Override
    public void onBrowserEvent(DomEvent event) {
        if(event.getTypeInt() == Event.ONCLICK) {
            event.preventDefault();
            clickHandler.onClicked();
        }
    }
}
