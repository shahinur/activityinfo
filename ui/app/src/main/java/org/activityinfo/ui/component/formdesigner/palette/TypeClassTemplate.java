package org.activityinfo.ui.component.formdesigner.palette;

import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resources;
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
        FormField formField = new FormField(Resources.generateId());
        formField.setType(typeClass.createType());
        formField.setLabel(typeClass.getLabel());
        return formField;
    }
}
