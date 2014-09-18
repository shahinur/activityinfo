package org.activityinfo.ui.style;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.Event;
import org.activityinfo.ui.vdom.shared.dom.DomEvent;
import org.activityinfo.ui.vdom.shared.html.CssClass;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

public class Button extends VComponent {

    private ButtonStyle style;
    private ButtonSize size;
    private VTree[] content;
    private boolean enabled = true;
    private boolean block = false;
    private List<CssClass> cssClasses = Lists.newArrayList();
    private PropMap propMap;

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

    public Button(PropMap propMap, VTree[] content) {
        this.propMap = propMap;
        this.content = content;
    }

    public ButtonStyle getStyle() {
        return style;
    }

    public void setStyle(ButtonStyle style) {
        this.style = style;
    }

    public ButtonSize getSize() {
        return size;
    }

    public void setSize(ButtonSize size) {
        this.size = size;
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

    public PropMap getPropMap() {
        return propMap;
    }

    public void setPropMap(PropMap propMap) {
        this.propMap = propMap;
    }

    public Button addCssClass(CssClass cssClass) {
        cssClasses.add(cssClass);
        return this;
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
        PropMap properties = propMap != null ? propMap : PropMap.withClasses(style.getClassNames());
        if(size != null) {
            properties.addClassName(size.getClassNames());
        }
        if(!enabled) {
            properties.set("disabled", "disabled");
        }
        if(block) {
            properties.addClassName(BaseStyles.BTN_BLOCK);
        }
        properties.addClassNames(cssClasses);

        return new VNode(HtmlTag.BUTTON, properties, content);
    }
}
