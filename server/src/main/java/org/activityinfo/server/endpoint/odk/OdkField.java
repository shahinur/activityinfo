package org.activityinfo.server.endpoint.odk;

import org.activityinfo.model.form.FormField;

public class OdkField {

    private FormField model;
    private OdkFormFieldBuilder builder;

    public OdkField(FormField model, OdkFormFieldBuilder builder) {
        this.model = model;
        this.builder = builder;
    }

    public FormField getModel() {
        return model;
    }

    public OdkFormFieldBuilder getBuilder() {
        return builder;
    }

    public String getAbsoluteFieldName() {
        return "/data/" + getRelativeFieldName();
    }

    public String getRelativeFieldName() {
        return "field_" + model.getId().asString();
    }
}
