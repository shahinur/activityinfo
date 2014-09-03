package org.activityinfo.ui.app.client.form.store;

import com.google.common.collect.Lists;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.ui.widget.validation.ValidationMessage;
import org.activityinfo.ui.widget.validation.ValidationSeverity;

import java.util.List;

public class FieldState {

    private final FormField field;
    private FieldValue value;
    private List<ValidationMessage> validationMessages = Lists.newArrayList();

    public FieldState(FormField field) {
        this.field = field;
    }

    public FormField getField() {
        return field;
    }

    public FieldValue getValue() {
        return value;
    }

    public void setValue(FieldValue value) {
        this.value = value;
    }

    public boolean isValid() {
        return validationMessages.isEmpty();
    }

    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }

    public void updateValue(FieldValue value) {
        this.value = value;
        validate();
    }


    public boolean validate() {
        validationMessages.clear();
        if(field.isRequired() && value == null) {
            validationMessages.add(new ValidationMessage(I18N.CONSTANTS.requiredFieldMessage(),
                ValidationSeverity.ERROR));
            return false;
        }
        return true;
    }


    public void clearValue() {

    }
}
