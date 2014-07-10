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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.model.form.FormField;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;
import org.activityinfo.ui.client.component.formdesigner.event.WidgetContainerSelectionEvent;

/**
 * @author yuriyz on 7/8/14.
 */
public class WidgetContainer {

    private static OurUiBinder uiBinder = GWT
            .create(OurUiBinder.class);

    interface OurUiBinder extends UiBinder<Widget, WidgetContainer> {
    }

    private EventBus eventBus;
    private FormFieldWidget formFieldWidget;
    private FormField formField;

    @UiField
    Button removeButton;
    @UiField
    FocusPanel focusPanel;
    @UiField
    HTML label;
    @UiField
    SimplePanel widgetContainer;

    public WidgetContainer(EventBus eventBus, FormFieldWidget formFieldWidget, FormField formField) {
        uiBinder.createAndBindUi(this);
        this.eventBus = eventBus;
        this.formFieldWidget = formFieldWidget;
        this.formField = formField;
        this.eventBus.addHandler(WidgetContainerSelectionEvent.TYPE, new WidgetContainerSelectionEvent.Handler() {
            @Override
            public void handle(WidgetContainerSelectionEvent event) {
                setSelected(false);
            }
        });

        widgetContainer.add(formFieldWidget);
        focusPanel.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WidgetContainer.this.onClick();
            }
        });
        syncWithModel();
    }

    public void syncWithModel() {
        label.setHTML(formField.getLabel());
        formFieldWidget.setType(formField.getType());
    }

    @UiHandler("removeButton")
    public void onRemove(ClickEvent clickEvent) {
        focusPanel.removeFromParent();
    }

    private void onClick() {
        eventBus.fireEvent(new WidgetContainerSelectionEvent(this));
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                setSelected(true);
            }
        });
    }

    public HTML getLabel() {
        return label;
    }

    public FormFieldWidget getFormFieldWidget() {
        return formFieldWidget;
    }

    public Widget asWidget() {
        return focusPanel;
    }

    public FormField getFormField() {
        return formField;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            focusPanel.addStyleName(FormDesignerStyles.INSTANCE.widgetContainerSelected());
        } else {
            focusPanel.removeStyleName(FormDesignerStyles.INSTANCE.widgetContainerSelected());
        }
    }
}
