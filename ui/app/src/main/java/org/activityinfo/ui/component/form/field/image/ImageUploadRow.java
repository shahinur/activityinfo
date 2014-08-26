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

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.image.ImageRowValue;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author yuriyz on 8/12/14.
 */
public class ImageUploadRow extends Composite {

    private static final Logger LOGGER = Logger.getLogger(ImageUploadRow.class.getName());

    interface OurUiBinder extends UiBinder<FormPanel, ImageUploadRow> {
    }

    private static OurUiBinder ourUiBinder = GWT.create(OurUiBinder.class);

    private final ImageRowValue value;
    private final String fieldId;
    private final String resourceId;

    @UiField
    FileUpload fileUpload;
    @UiField
    HTMLPanel imageContainer;
    @UiField
    ImageElement loadingImage;
    @UiField
    Button downloadButton;
    @UiField
    Button removeButton;
    @UiField
    VerticalPanel formFieldsContainer;
    @UiField
    Button addButton;
    @UiField
    FormPanel formPanel;

    public ImageUploadRow(ImageRowValue value, String fieldId, String resourceId) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.value = value;
        this.fieldId = fieldId;
        this.resourceId = resourceId;

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

        if (value.getBlobId() != null) {
            imageContainer.setVisible(false);
            downloadButton.setVisible(true);
        }
    }

    public void setReadOnly(boolean readOnly) {
        fileUpload.setEnabled(!readOnly);
        downloadButton.setEnabled(!readOnly);
        removeButton.setEnabled(!readOnly);
        addButton.setEnabled(!readOnly);
    }

    private String createUploadUrl() {
        String blobId = Resources.generateId().asString();
//        String classId = formClassId.asString();
//        String fieldId = formField.getId().asString();
        String fileName = fileName();
        String mimeType = MimeTypeUtil.mimeTypeFromFileExtension(fileExtension(fileName));
        value.setMimeType(mimeType);
        value.setFilename(fileName);
        value.setBlobId(blobId);
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
//                    String json = response.getText();
//                    Resource resource = Resources.fromJson(json);
//                    UploadCredentials uploadCredentials = UploadCredentials.fromRecord(resource);
//
//                    Map<String, String> formFields = uploadCredentials.getFormFields();
//                    for (Map.Entry<String, String> field : formFields.entrySet()) {
//                        formFieldsContainer.add(new Hidden(field.getKey(), field.getValue()));
//                    }
//
//                    formPanel.setAction(uploadCredentials.getUrl());
//                    formPanel.setMethod(uploadCredentials.getMethod());
//                    upload();

                    throw new UnsupportedOperationException();
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    LOGGER.log(Level.SEVERE, "Failed to send request", exception);
                }
            });
        } catch (RequestException e) {
            LOGGER.log(Level.SEVERE, "Failed to send request", e);
        }
    }

    private void upload() {
        imageContainer.setVisible(true);
        downloadButton.setVisible(false);

        formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                String responseString = event.getResults(); // what about fail results?

                imageContainer.setVisible(false);
                downloadButton.setVisible(true);
            }
        });
        formPanel.submit();
    }

    private void download() {
        StringBuilder stringBuilder = new StringBuilder("/service/blob/");
        stringBuilder.append(resourceId);
        stringBuilder.append("/");
        stringBuilder.append(fieldId);
        stringBuilder.append("/");
        stringBuilder.append(value.getBlobId());
        stringBuilder.append("/thumbnail?width=");
        stringBuilder.append(value.getWidth());
        stringBuilder.append("&height=");
        stringBuilder.append(value.getHeight());
        Window.open(stringBuilder.toString(), "_blank", null);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    public ImageRowValue getValue() {
        return value;
    }
}
