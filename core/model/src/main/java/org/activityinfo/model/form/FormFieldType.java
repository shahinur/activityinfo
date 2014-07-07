package org.activityinfo.model.form;

import org.activityinfo.model.type.*;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.time.LocalDateType;

/**
 * The type of field, which influences how input is presented
 * the user, how it is validated, and what default measures
 * are available.
 */
public class FormFieldType {

    private FormFieldType() {}


    /**
     * Defined exact length of string to differ between FREE_TEXT and NARRATIVE types.
     * If string length less than #FREE_TEXT_LENGTH then type is #FREE_TEXT otherwise it is NARRATIVE.
     */
    public static final int FREE_TEXT_LENGTH = 80;


    public static FieldTypeClass valueOf(String name) {
        switch(name) {
            case "QUANTITY":
                return QuantityType.TypeClass.INSTANCE;
            case "NARRATIVE":
                return NarrativeType.INSTANCE;
            case "FREE_TEXT":
                return TextType.INSTANCE;
            case "LOCAL_DATE":
                return TextType.INSTANCE;
            case "GEOGRAPHIC_POINT":
                return GeoPointType.INSTANCE;
            case "REFERENCE":
                return ReferenceType.TypeClass.INSTANCE;
        }
        throw new IllegalArgumentException("name: " + name);
    }

    public static FieldTypeClass[] values() {
        return new FieldTypeClass[] {
                FieldTypeClass.QUANTITY,
                FieldTypeClass.NARRATIVE,
                FieldTypeClass.FREE_TEXT,
                FieldTypeClass.LOCAL_DATE,
                FieldTypeClass.GEOGRAPHIC_POINT};

    }
}
