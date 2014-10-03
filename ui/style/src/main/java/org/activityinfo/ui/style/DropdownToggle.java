package org.activityinfo.ui.style;

import com.google.gwt.user.client.Event;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class DropdownToggle extends VComponent {

    private ClickHandler clickHandler;
    private ButtonStyle buttonStyle = ButtonStyle.DEFAULT;
    private VTree[] content;

    DropdownToggle(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    public ButtonStyle getButtonStyle() {
        return buttonStyle;
    }

    public void setButtonStyle(ButtonStyle buttonStyle) {
        this.buttonStyle = buttonStyle;
    }

    public VTree[] getContent() {
        return content;
    }

    public void setContent(VTree... content) {
        this.content = content;
    }

    @Override
    protected VTree render() {
        return new VNode(HtmlTag.BUTTON,
            H.className(buttonStyle.getClassNames() + BaseStyles.DROPDOWN_TOGGLE + BaseStyles.TP_ICON), content);
    }

    @Override
    public int getEventMask() {
        return Event.ONCLICK;
    }

    @Override
    public void onBrowserEvent(DomEvent event) {
        clickHandler.onClicked();
    }
}
