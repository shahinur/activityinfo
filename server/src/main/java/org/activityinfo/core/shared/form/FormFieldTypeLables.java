package org.activityinfo.core.shared.form;

import org.activityinfo.legacy.shared.Log;
import org.activityinfo.model.form.FormFieldType;

import static org.activityinfo.i18n.shared.I18N.CONSTANTS;

/**
 * Created by alex on 3/14/14.
 */
public class FormFieldTypeLables {
    public static String getFormFieldType(FormFieldType type) {
        switch (type) {
            case REFERENCE:
                return CONSTANTS.fieldTypeReference();
            case QUANTITY:
                return CONSTANTS.fieldTypeQuantity();
            case LOCAL_DATE:
                return CONSTANTS.fieldTypeDate();
            case FREE_TEXT:
                return CONSTANTS.fieldTypeText();
            case GEOGRAPHIC_POINT:
                return CONSTANTS.fieldTypeGeographicPoint();
            case NARRATIVE:
                return CONSTANTS.fieldTypeBigText();
        }
        Log.warn("No translation for " + type);
        return type.name().toLowerCase();
    }
}
