package org.activityinfo.ui.style;

import com.google.gwt.user.client.Event;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.*;

public class CloseButton extends VComponent {

    private ClickHandler clickHandler;
    private FloatStyle floatStyle = FloatStyle.NONE;

    public CloseButton() {
    }

    public CloseButton(FloatStyle floatStyle) {
        this.floatStyle = floatStyle;
    }

    @Override
    protected VTree render() {
        PropMap props = PropMap.withClasses(BaseStyles.CLOSE, floatStyle.className()).ariaHidden();
        return new VNode(HtmlTag.BUTTON, props, new VText("×"));
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
