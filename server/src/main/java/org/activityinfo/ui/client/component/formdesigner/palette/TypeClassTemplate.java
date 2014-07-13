package org.activityinfo.ui.client.component.formdesigner.palette;

import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldTypeClass;

/**
 * FieldTemplate which creates a new FormField using the
 * default instance of a given TypeClass
 */
public class TypeClassTemplate implements FieldTemplate {

    public FieldTypeClass typeClass;

    public TypeClassTemplate(FieldTypeClass typeClass) {
        this.typeClass = typeClass;
    }

    @Override
    public String getLabel() {
        return typeClass.getLabel();
    }

    @Override
    public FormField createField() {
        FormField formField = new FormField(ResourceId.generateId());
        formField.setType(typeClass.createType());
        return formField;
    }
}
