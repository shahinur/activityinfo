package org.activityinfo.model.type;


import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;

public interface FieldType {

    /**
     * @return the {@code FieldTypeClass} of which this {@code FieldType}
     * is a member
     */
    FieldTypeClass getTypeClass();


    <T> T accept(FormField field, FormClassVisitor<T> visitor);

}
