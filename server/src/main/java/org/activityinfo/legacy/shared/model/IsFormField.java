package org.activityinfo.legacy.shared.model;

import org.activityinfo.model.type.FieldTypeClass;

import java.io.Serializable;

public interface IsFormField extends Serializable {

    /**
     *
     * @return this form field's label
     */
    String getLabel();

    /**
     *
     * @return the defined sort order index of the form field.
     */
    int getSortOrder();

    FieldTypeClass getTypeClass();

    boolean isRequired();

    String getFieldId();

    String getDescription();
}
