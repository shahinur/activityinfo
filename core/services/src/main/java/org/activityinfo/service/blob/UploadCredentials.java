package org.activityinfo.service.blob;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import org.activityinfo.model.record.IsRecord;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.record.RecordBuilder;
import org.activityinfo.model.record.Records;
import org.activityinfo.model.resource.PropertyBag;

import java.util.Map;

/**
 * Credentials that can be used upload a blob from the client
 */
public class UploadCredentials implements IsRecord {

    @JsonProperty
    private String url;

    @JsonProperty
    private String method;

    @JsonProperty
    private Map<String, String> formFields = Maps.newHashMap();

    @JsonCreator
    private UploadCredentials() {
    }

    public UploadCredentials(String url, String method, Map<String, String> formFields) {
        this.url = url;
        this.method = method;
        this.formFields = formFields;
    }

    /**
     * @return the URL to which the upload should be submitted
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @return the method (POST or PUT) that the file should be submitted.
     */
    public String getMethod() {
        return method;
    }

    /**
     *
     * @return a set of metadata that should be included in the form submission.
     */
    public Map<String, String> getFormFields() {
        return formFields;
    }

    @Override
    public String toString() {
        StringBuilder form = new StringBuilder();
        form.append("<form action=\"" + url + "\" method=\"" + method + "\">\n");
        for(String fieldName : formFields.keySet()) {
            form.append("<input type=\"hidden\" name=\"" + fieldName + "\"" +
                        " value=\"" + formFields.get(fieldName) + "\">\n");
        }
        form.append("</form>");
        return form.toString();
    }

    @Override
    public Record asRecord() {
        RecordBuilder formFieldsRecord = Records.builder();
        for (Map.Entry<String,String> entry : formFields.entrySet()) {
            formFieldsRecord.set(entry.getKey(), entry.getValue());
        }
        return Records.builder()
                .set("url", url)
                .set("method", method)
                .set("formFields", formFieldsRecord.build())
                .build();
    }

    public static UploadCredentials fromRecord(PropertyBag<? extends PropertyBag> record) {
        Map<String, String> formFields = Maps.newHashMap();
        Record formFieldRecord = record.getRecord("formFields");
        for (Map.Entry<String, Object> property : formFieldRecord.asMap().entrySet()) {
            formFields.put(property.getKey(), (String) property.getValue());
        }
        return new UploadCredentials(record.getString("url"), record.getString("method"), formFields);
    }
}
