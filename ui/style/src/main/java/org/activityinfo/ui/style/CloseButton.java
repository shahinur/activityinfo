package org.activityinfo.ui.style;

import com.google.gwt.user.client.Event;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VText;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.className;

public class CloseButton extends VComponent {

    private ClickHandler clickHandler;


    @Override
    protected VTree render() {
        return new VNode(HtmlTag.BUTTON,
            className(BaseStyles.CLOSE).ariaHidden().data("dismiss", "alert"),
            new VText("×"));
    }

    @Override
    public int getEventMask() {
        return Event.ONCLICK;
    }

    @Override
    public void onBrowserEvent(DomEvent event) {
        if(clickHandler != null) {
            clickHandler.onClicked();
        }
    }

    public ClickHandler getClickHandler() {
        return clickHandler;
    }

    public void setClickHandler(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }
}
