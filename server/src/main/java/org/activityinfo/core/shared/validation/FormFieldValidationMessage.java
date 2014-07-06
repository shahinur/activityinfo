package org.activityinfo.core.shared.validation;

import org.activityinfo.model.resource.ResourceId;

/**
 * Validation Error bound to form field.
 */
public class FormFieldValidationMessage extends ValidationMessage {

    private ResourceId fieldId;

    public FormFieldValidationMessage(ResourceId fieldId) {
        this.fieldId = fieldId;
    }

    public ResourceId getFieldId() {
        return fieldId;
    }
}
