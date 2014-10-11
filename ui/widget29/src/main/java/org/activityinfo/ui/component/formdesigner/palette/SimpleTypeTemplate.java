package org.activityinfo.ui.component.formdesigner.palette;

import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.ParametrizedFieldType;


/**
 * FieldTemplate which creates a new FormField using the
 * default instance of a given TypeClass
 */
public class SimpleTypeTemplate implements FieldTemplate {

    public FieldType type;
    private String label;

    public SimpleTypeTemplate(FieldType type, String label) {
        this.type = type;
        this.label = label;
        assert ! (type instanceof ParametrizedFieldType) : "type must be a singleton";
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public FormField createField() {
        FormField formField = new FormField(Resources.generateId());
        formField.setType(type);
        formField.setLabel(label);
        return formField;
    }
}
