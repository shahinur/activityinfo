package org.activityinfo.ui.client.component.form.field;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.image.ImageValue;
import org.activityinfo.promise.Promise;

/**
 * @author yuriyz on 8/7/14.
 */
public class ImageUploadFieldWidget implements FormFieldWidget<ImageValue> {

    interface OurUiBinder extends UiBinder<FormPanel, ImageUploadFieldWidget> {
    }

    private static OurUiBinder ourUiBinder = GWT.create(OurUiBinder.class);

    private final FormPanel formPanel;

    @UiField
    FileUpload fileUpload;
    @UiField
    Button uploadButton;

    public ImageUploadFieldWidget(final ValueUpdater valueUpdater) {

        formPanel = ourUiBinder.createAndBindUi(this);
        formPanel.setAction(createActionPath());
        formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                String results = event.getResults();
                // todo handle results
            }
        });

        uploadButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                formPanel.submit();
            }
        });
    }

    private String createActionPath() {
        String blobId = "a";//UUID.uuid();
        String classId = "classId"; // todo
        String fieldId = "fieldId";
        return "/service/blob/" + classId + "/" + fieldId + "/" + blobId;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        fileUpload.setEnabled(!readOnly);
    }

    @Override
    public Promise<Void> setValue(ImageValue value) {
        return Promise.done();
    }

    @Override
    public void setType(FieldType type) {
        // ignore
    }

    @Override
    public void clearValue() {

    }

    @Override
    public Widget asWidget() {
        return formPanel;
    }
}
