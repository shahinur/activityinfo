package org.activityinfo.ui.component.formdesigner.palette;

import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.ReferenceType;

public class ReferenceTemplate implements FieldTemplate {
    @Override
    public String getLabel() {
        return "Reference/Link";
    }

    @Override
    public FormField createField() {
        FormField field = new FormField(ResourceId.generateId());
        field.setLabel("Reference to another form");
        field.setType(new ReferenceType().setCardinality(Cardinality.SINGLE));
        return field;
    }
}
