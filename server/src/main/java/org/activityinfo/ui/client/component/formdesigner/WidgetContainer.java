package org.activityinfo.ui.client.component.formdesigner;
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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;
import org.activityinfo.ui.client.component.formdesigner.event.SelectionEvent;

/**
 * @author yuriyz on 7/8/14.
 */
public class WidgetContainer {

    private EventBus eventBus;
    private FormFieldWidget formFieldWidget;
    private Widget containerWidget;

    public WidgetContainer(EventBus eventBus, FormFieldWidget widget) {
        this.eventBus = eventBus;
        this.formFieldWidget = widget;
        this.containerWidget = createContainerWidget();
        this.eventBus.addHandler(SelectionEvent.getType(), new SelectionHandler<Object>() {
            @Override
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Object> event) {
                setSelected(false);
            }
        });
    }

    private Widget createContainerWidget() {
        final Button removeButton = new Button("x");
        removeButton.setStyleName("close pull-right btn-link");
        removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onRemove();
            }
        });


        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setWidth("100%");
        horizontalPanel.add(formFieldWidget);
        horizontalPanel.add(removeButton);

        final FocusPanel focusPanel = new FocusPanel(horizontalPanel);
        focusPanel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WidgetContainer.this.onClick();
            }
        });
        focusPanel.addStyleName(FormDesignerStyles.INSTANCE.widgetContainer());
        return focusPanel;
    }

    private void onRemove() {
        containerWidget.removeFromParent();
    }

    private void onClick() {
        eventBus.fireEvent(new SelectionEvent(this));
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                setSelected(true);
            }
        });
    }

    public FormFieldWidget getFormFieldWidget() {
        return formFieldWidget;
    }

    public Widget asWidget() {
        return containerWidget;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            containerWidget.addStyleName(FormDesignerStyles.INSTANCE.widgetContainerSelected());
        } else {
            containerWidget.removeStyleName(FormDesignerStyles.INSTANCE.widgetContainerSelected());
        }
    }
}
