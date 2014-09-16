package org.activityinfo.ui.style;

import com.google.gwt.user.client.Event;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class Button extends VComponent {

    private ButtonStyle style;
    private ButtonSize size;
    private VTree[] content;
    private boolean enabled = true;
    private boolean block = false;

    private ClickHandler clickHandler;

    public Button(ButtonStyle style, ButtonSize size) {
        this.style = style;
        this.size = size;
    }

    public Button(ButtonStyle style, ButtonSize size, VTree... content) {
        this.style = style;
        this.size = size;
        this.content = content;
    }

    public Button(ButtonStyle style, VTree... content) {
        this.style = style;
        this.content = content;
    }

    public VTree[] getContent() {
        return content;
    }

    public void setContent(VTree... content) {
        this.content = content;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public void setClickHandler(ClickHandler clickHandler) {
        this.clickHandler = clickHandler;
    }

    @Override
    public int getEventMask() {
        return clickHandler == null ? 0 : Event.ONCLICK;
    }

    @Override
    public void onBrowserEvent(DomEvent event) {
        if(clickHandler != null && event.getTypeInt() == Event.ONCLICK) {
            clickHandler.onClicked();
        }
    }

    @Override
    protected VTree render() {
        PropMap properties = PropMap.withClasses(style.getClassNames());
        if(size != null) {
            properties.addClassName(size.getClassNames());
        }
        if(!enabled) {
            properties.set("disabled", "disabled");
        }
        if(block) {
            properties.addClassName(BaseStyles.BTN_BLOCK);
        }

        return new VNode(HtmlTag.BUTTON, properties, content);
    }
}
