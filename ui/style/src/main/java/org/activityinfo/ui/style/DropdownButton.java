package org.activityinfo.ui.style;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.collect.Lists;
import com.google.gwt.user.client.Event;
import org.activityinfo.ui.vdom.shared.html.AriaRole;
import org.activityinfo.ui.vdom.shared.html.Children;
import org.activityinfo.ui.vdom.shared.html.CssClass;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.Arrays;
import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.*;

/**
 * ATTENTION : not finished !
 *
 * @author yuriyz on 9/15/14.
 */
public class DropdownButton extends VComponent {

    private ButtonStyle style;
    private ButtonSize size;
    private VTree[] labelContent;
    private Button[] buttons;
    private boolean enabled = true;
    private List<CssClass> cssClasses = Lists.newArrayList();

    // state flags
//    private boolean showPopupOnClick = true;

    public DropdownButton(ButtonStyle style, ButtonSize size) {
        this.style = style;
        this.size = size;
    }

    public DropdownButton(ButtonStyle style, ButtonSize size, VTree[] labelContent, Button... buttons) {
        this.style = style;
        this.size = size;
        this.buttons = buttons;
        this.labelContent = labelContent;
    }

    public Button[] getButtons() {
        return buttons;
    }

    public void setButtons(Button[] buttons) {
        this.buttons = buttons;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public DropdownButton addCssClass(CssClass cssClass) {
        cssClasses.add(cssClass);
        return this;
    }

    @Override
    public int getEventMask() {
        return buttons == null ? 0 : Event.ONCLICK;
    }

    public VTree[] getLabelContent() {
        return labelContent;
    }

    public void setLabelContent(VTree[] labelContent) {
        this.labelContent = labelContent;
    }

    //    @Override
//    public void onBrowserEvent(DomEvent event) {
//        if(clickHandler != null && event.getTypeInt() == Event.ONCLICK) {
//            clickHandler.onClicked();
//        }
//    }

    public static VNode caret() {
        return new VNode(HtmlTag.SPAN, className(BaseStyles.CARET));
    }

    @Override
    protected VTree render() {
        List<VTree> contentList = Lists.newArrayList();
        if (labelContent != null) {
            contentList.addAll(Arrays.asList(labelContent));
        }
        contentList.add(caret());
        Button button = new Button(style().data("toggle", "dropdown"), Children.toArray(contentList));
        button.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                refresh();
            }
        });
        return div(PropMap.withClasses(BaseStyles.BTN_GROUP, BaseStyles.PULL_RIGHT),
                button,
                new VNode(HtmlTag.BUTTON, style().data("toggle", "dropdown"), contentList), dropdown());
    }

    private VTree dropdown() {
        if (buttons != null) {
            List<VTree> vTrees = Lists.newArrayList();
            for (Button button : buttons) {
                vTrees.add(li(button));
            }
            return ul(PropMap.withClasses(BaseStyles.DROPDOWN_MENU).role(AriaRole.MENU), Children.toArray(vTrees));
        }
        return ul(BaseStyles.DROPDOWN_MENU);
    }

    private PropMap style() {
        PropMap properties = PropMap.withClasses(style.getClassNames());
        if (size != null) {
            properties.addClassName(size.getClassNames());
        }
        if (!enabled) {
            properties.set("disabled", "disabled");
        }
        properties.addClassName(BaseStyles.DROPDOWN_TOGGLE);
        properties.addClassNames(cssClasses);
        return properties;
    }

}