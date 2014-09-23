package org.activityinfo.ui.app.client.request;

import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.RootPanel;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.ui.store.remote.client.StatusCodeException;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlobUploader {

    private static final Logger LOGGER = Logger.getLogger(BlobUploader.class.getName());

    private static final String UPLOAD_REQUEST_URL = "/service/blob";

    private static BlobUploader instance;

    private final FormPanel formPanel;
    private final FileUpload fileUpload;

    private Promise<UploadResult> currentRequest = null;
    private BlobId currentBlobId = null;
    private boolean fileSelected = false;

    private Map<String, Hidden> hiddenFields = Maps.newHashMap();

    public static Promise<UploadResult> uploadBlob() {
        if(instance == null) {
            instance = new BlobUploader();
        }
        return instance.upload();
    }


    private BlobUploader() {

        fileUpload = new FileUpload();
        fileUpload.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                onFileChanged(event);
            }
        });

        formPanel = new FormPanel();
        formPanel.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        formPanel.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
        formPanel.add(fileUpload);
        formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(FormPanel.SubmitCompleteEvent event) {
                onUploadComplete();
            }
        });

        RootPanel.get().add(formPanel);
    }

    public Promise<UploadResult> upload() {

        if(currentRequest != null && !currentRequest.isSettled() && fileSelected) {
            return Promise.rejected(new IllegalStateException("Upload already in progress"));
        }

        currentBlobId = BlobId.generate();
        fileSelected = false;
        triggerUpload(fileUpload.getElement());

        currentRequest = new Promise<>();

        return currentRequest;
    }

    private void onFileChanged(ChangeEvent event) {

        fileSelected = true;

        try {
            requestUploadUrl();
        } catch(Exception e) {
            currentRequest.reject(e);
        }
    }

    private static native void triggerUpload(Element element) /*-{
        element.click();
    }-*/;


    private void requestUploadUrl() {
        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, UPLOAD_REQUEST_URL);
        requestBuilder.setHeader("Content-type", "application/x-www-form-urlencoded");
        requestBuilder.setRequestData("blobId=" + currentBlobId.asString() + "&filename=" + URL.encode(fileUpload.getFilename()));
        requestBuilder.setCallback(new RequestCallback() {
            @Override
            public void onResponseReceived(com.google.gwt.http.client.Request request, Response response) {
                if(response.getStatusCode() != 200) {
                    currentRequest.reject(new StatusCodeException(response.getStatusCode()));
                } else {
                    JSONObject credentials = JSONParser.parseStrict(response.getText()).isObject();
                    executeUpload(credentials);
                }
            }

            @Override
            public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                LOGGER.log(Level.SEVERE, "Failed to send request", exception);
                currentRequest.reject(exception);
            }
        });

        try {
            requestBuilder.send();

        } catch (RequestException e) {
            LOGGER.log(Level.SEVERE, "Failed to send request", e);
            currentRequest.reject(e);
        }
    }

    private void executeUpload(JSONObject credentials) {
        updateCredentials(credentials);
        formPanel.submit();
    }

    private void updateCredentials(JSONObject credentials) {
        JSONObject formFields = credentials.get("formFields").isObject();
        for (String fieldName : formFields.keySet()) {
            Hidden field = hiddenFields.get(fieldName);
            if(field == null) {
                field = new Hidden(fieldName);
                hiddenFields.put(fieldName, field);
            }
            field.setValue(formFields.get(fieldName).isString().stringValue());
        }

        formPanel.setAction(credentials.get("url").isString().stringValue());
        formPanel.setMethod(credentials.get("method").isString().stringValue());
    }

    private void onUploadComplete() {
        currentRequest.resolve(new UploadResult(currentBlobId));
    }
}
