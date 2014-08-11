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

import com.google.common.base.Strings;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.core.shared.util.MimeTypeUtil;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.image.ImageValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.UploadCredentials;

import java.util.Map;

/**
 * @author yuriyz on 8/7/14.
 */
public class ImageUploadFieldWidget implements FormFieldWidget<ImageValue> {

    interface OurUiBinder extends UiBinder<FormPanel, ImageUploadFieldWidget> {
    }

    private static OurUiBinder ourUiBinder = GWT.create(OurUiBinder.class);

    private final FormPanel formPanel;
    private final FormField formField;
    private final ResourceId formClassId;
    private ImageValue value = null;

    @UiField
    FileUpload fileUpload;
    @UiField
    HTMLPanel imageContainer;
    @UiField
    ImageElement loadingImage;
    @UiField
    Button downloadButton;
    @UiField
    HTMLPanel downloadButtonContainer;

    public ImageUploadFieldWidget(ResourceId formClassId, FormField formField, final ValueUpdater valueUpdater) {
        this.formClassId = formClassId;
        this.formField = formField;

        formPanel = ourUiBinder.createAndBindUi(this);

        fileUpload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                requestUploadUrl();
            }
        });
        downloadButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                download();
            }
        });
    }

    private String createUploadUrl() {
        String blobId = ResourceId.generateId().asString();
//        String classId = formClassId.asString();
//        String fieldId = formField.getId().asString();
        String fileName = fileName();
        String mimeType = MimeTypeUtil.mimeTypeFromFileExtension(fileExtension(fileName));
        this.value = new ImageValue(mimeType, fileName, blobId);
        return "/service/blob/" + blobId;
    }

    public static String fileExtension(String filename) {
        int i = filename.lastIndexOf(".");
        if (i != -1) {
            return filename.substring(i + 1);
        }
        return filename;
    }

    private String fileName() {
        final String filename = fileUpload.getFilename();
        if (Strings.isNullOrEmpty(filename)) {
            return "unknown";
        }

        int i = filename.lastIndexOf("/");
        if (i == -1) {
            i = filename.lastIndexOf("\\");
        }
        if (i != -1 && (i + 1) < filename.length()) {
            return filename.substring(i + 1);
        }
        return filename;
    }

    private void requestUploadUrl() {
        try {
            RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, URL.encode(createUploadUrl()));
            requestBuilder.sendRequest(null, new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    String json = response.getText();
                    Resource resource = Resources.fromJson(json);
                    UploadCredentials uploadCredentials = UploadCredentials.fromRecord(resource);

                    VerticalPanel formFieldsContainer = new VerticalPanel();
                    Map<String,String> formFields = uploadCredentials.getFormFields();
                    for (Map.Entry<String, String> field: formFields.entrySet()) {
                        formFieldsContainer.add(new Hidden(field.getKey(), field.getValue()));
                    }

                    formPanel.setWidget(formFieldsContainer);
                    formPanel.setAction(uploadCredentials.getUrl());
                    formPanel.setMethod(uploadCredentials.getMethod());
                    upload();
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    Log.error("Failed to send request", exception);
                }
            });
        } catch (RequestException e) {
            Log.error("Failed to send request", e);
        }
    }

    private void upload() {
        imageContainer.setVisible(true);
        downloadButtonContainer.setVisible(false);

        formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                String responseString = event.getResults(); // what about fail results?

                imageContainer.setVisible(false);
                downloadButtonContainer.setVisible(true);
            }
        });
        formPanel.submit();
    }

    private void download() {
        // todo
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
