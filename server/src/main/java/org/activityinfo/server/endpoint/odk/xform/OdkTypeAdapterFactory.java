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
        if (fieldType instanceof BooleanType) return new BooleanTypeAdapterImpl();
        if (fieldType instanceof EnumType) return new EnumTypeAdapterImpl((EnumType) fieldType);
        if (fieldType instanceof GeoPointType) return new GeoPointTypeAdapterImpl();
        if (fieldType instanceof LocalDateType) return new LocalDateTypeAdapterImpl();
        if (fieldType instanceof NarrativeType) return new NarrativeTypeAdapterImpl();
        if (fieldType instanceof QuantityType) return new QuantityTypeAdapterImpl((QuantityType) fieldType);
        if (fieldType instanceof ReferenceType) return new ReferenceTypeAdapterImpl((ReferenceType) fieldType);
        if (fieldType instanceof TextType) return new TextTypeAdapterImpl();

        // If this happens, it means this class needs to be expanded to support the new FieldType class.
        throw new IllegalArgumentException("Unknown FieldType object passed to OdkTypeAdapterFactory.fromFieldType()!");
    }
}
