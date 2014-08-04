package org.activityinfo.server.endpoint.odk.xform;

import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateType;

public class OdkTypeAdapterFactory {
    public static OdkTypeAdapter fromFieldType(FieldType fieldType) {
        SelectOptions selectOptions = getSelectOptions(fieldType);

        if (fieldType instanceof BooleanType) return new SelectAdapter("boolean", selectOptions);
        if (fieldType instanceof EnumType) return new SelectAdapter("string", selectOptions);
        if (fieldType instanceof GeoPointType) return new SimpleInputAdapter("geopoint");
        if (fieldType instanceof LocalDateType) return new SimpleInputAdapter("date");
        if (fieldType instanceof NarrativeType) return new SimpleInputAdapter("string");
        if (fieldType instanceof QuantityType) return new QuantityTypeAdapter((QuantityType) fieldType);
        if (fieldType instanceof ReferenceType) return new SelectAdapter("string", selectOptions);
        if (fieldType instanceof TextType) return new SimpleInputAdapter("string");

        // If this happens, it means this class needs to be expanded to support the new FieldType class.
        throw new IllegalArgumentException("Unknown FieldType object passed to OdkTypeAdapterFactory.fromFieldType()!");
    }

    private static SelectOptions getSelectOptions(FieldType fieldType) {
        if (fieldType instanceof BooleanType) return new BooleanTypeSelectOptions();
        if (fieldType instanceof EnumType) return new EnumTypeSelectOptions((EnumType) fieldType);
        if (fieldType instanceof ReferenceType) return new ReferenceTypeSelectOptions((ReferenceType) fieldType);
        return null;
    }
}
