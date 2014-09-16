package org.activityinfo.ui.style;

import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Event;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class ToolButton extends ToolItem {

    private final Icon icon;
    private final String label;
    private ClickHandler clickHandler;

    public ToolButton(Icon icon, String label) {
        this.icon = icon;
        this.label = label;
    }

    public void setClickHandler(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    protected VTree render() {
        return li(a(href(UriUtils.fromTrustedString("#")), icon.render(), t(" "), t(label)));
    }

    @Override
    public int getEventMask() {
        return Event.ONCLICK;
    }

    @Override
    public void onBrowserEvent(DomEvent event) {
        if(event.getTypeInt() == Event.ONCLICK && clickHandler != null) {
            clickHandler.onClicked();
            event.preventDefault();
        }
    }
}
