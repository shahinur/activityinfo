package org.activityinfo.ui.component.form.field.image;
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

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.image.ImageRowValue;
import org.activityinfo.model.type.image.ImageValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.component.form.field.FormFieldWidget;

/**
 * @author yuriyz on 8/7/14.
 */
public class ImageUploadFieldWidget implements FormFieldWidget<ImageValue> {

    interface OurUiBinder extends UiBinder<HTMLPanel, ImageUploadFieldWidget> {
    }

    private static OurUiBinder ourUiBinder = GWT.create(OurUiBinder.class);

    private final HTMLPanel rootPanel;
    private final FormField formField;
    private ImageValue value = new ImageValue();

    public ImageUploadFieldWidget(FormField formField, final ValueUpdater valueUpdater) {
        this.formField = formField;
        rootPanel = ourUiBinder.createAndBindUi(this);

        addNewRow(new ImageRowValue());
    }

    private void addNewRow(final ImageRowValue rowValue) {
        final ImageUploadRow imageUploadRow = new ImageUploadRow(rowValue);
        imageUploadRow.addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                addNewRow(new ImageRowValue());
            }
        });

        imageUploadRow.removeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                setButtonsState();
                value.getValues().remove(imageUploadRow.getValue());
                rootPanel.remove(imageUploadRow);
            }
        });

        value.getValues().add(rowValue);
        rootPanel.add(imageUploadRow);

        setButtonsState();
    }

    private void setButtonsState() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                if (rootPanel.getWidgetCount() > 0 && rootPanel.getWidget(0) instanceof ImageUploadRow) {
                    // disable button if it's first row, otherwise user may be trapped in widget without any rows

                    ImageUploadRow firstUploadRow = (ImageUploadRow) rootPanel.getWidget(0);
                    firstUploadRow.removeButton.setEnabled(false);
                }
            }
        });
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        for (int i = 0; i < rootPanel.getWidgetCount(); i++) {
            IsWidget widget = rootPanel.getWidget(i);
            if (widget instanceof ImageUploadRow) {
                ImageUploadRow row = (ImageUploadRow) widget;
                row.setReadOnly(readOnly);
            }
        }
    }

    @Override
    public Promise<Void> setValue(ImageValue value) {
        if (value == null) {
            clearValue();
            return Promise.done();
        }

        this.value = value;
        rootPanel.clear();

        for (ImageRowValue rowValue : value.getValues()) {
            addNewRow(rowValue);
        }

        return Promise.done();
    }

    @Override
    public void setType(FieldType type) {
        // ignore
    }

    @Override
    public void clearValue() {
        value = new ImageValue();
        rootPanel.clear();
    }

    @Override
    public Widget asWidget() {
        return rootPanel;
    }
}
