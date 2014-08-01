package org.activityinfo.server.endpoint.odk.xform;

import org.activityinfo.model.type.*;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateType;

import static org.activityinfo.model.type.Cardinality.SINGLE;

public class OdkTypeAdapter {
    final private FieldType fieldType;
    final private FieldTypeEnum fieldTypeEnum;

    public OdkTypeAdapter(FieldType fieldType) {
        this.fieldType = fieldType;
        this.fieldTypeEnum = FieldTypeEnum.fromFieldType(fieldType);

        // If this happens, it means this class needs to be expanded to support the new FieldType class.
        if (fieldTypeEnum == null) throw new IllegalArgumentException("Unknown FieldType class passed to constructor!");
    }

    public boolean isBoolean() {
        return fieldTypeEnum == FieldTypeEnum.BOOLEAN;
    }

    public boolean isEnum() {
        return fieldTypeEnum == FieldTypeEnum.ENUM;
    }

    public boolean isGeoPoint() {
        return fieldTypeEnum == FieldTypeEnum.GEO_POINT;
    }

    public boolean isLocalDate() {
        return fieldTypeEnum == FieldTypeEnum.LOCAL_DATE;
    }

    public boolean isNarrative() {
        return fieldTypeEnum == FieldTypeEnum.NARRATIVE;
    }

    public boolean isQuantity() {
        return fieldTypeEnum == FieldTypeEnum.QUANTITY;
    }

    public boolean isReference() {
        return fieldTypeEnum == FieldTypeEnum.REFERENCE;
    }

    public boolean isText() {
        return fieldTypeEnum == FieldTypeEnum.TEXT;
    }

    public String getModelBindType() {
        switch (fieldTypeEnum) {
            case BOOLEAN:
                return "boolean";
            case ENUM:
                return "string";
            case GEO_POINT:
                return "geopoint";
            case LOCAL_DATE:
                return "date";
            case NARRATIVE:
                return "string";
            case QUANTITY:
                return "decimal";
            case REFERENCE:
                return "string";
            case TEXT:
                return "string";
            default:
                // This should never happen and will never happen, except in cases of HW / JVM bugs or reflection abuse.
                throw new IllegalStateException("Non-existent enum passed to getModelBindType() method!");
        }
    }

    public String getPresentationElement() {
        Cardinality cardinality;
        switch (fieldTypeEnum) {
            case BOOLEAN:
                return "select1";
            case ENUM:
                cardinality = ((EnumType) fieldType).getCardinality();
                return SINGLE.equals(cardinality) ? "select1" : "select";
            case GEO_POINT:
                return "input";
            case LOCAL_DATE:
                return "input";
            case NARRATIVE:
                return "input";
            case QUANTITY:
                return "input";
            case REFERENCE:
                cardinality = ((ReferenceType) fieldType).getCardinality();
                return SINGLE.equals(cardinality) ? "select1" : "select";
            case TEXT:
                return "input";
            default:
                // This should never happen and will never happen, except in cases of HW / JVM bugs or reflection abuse.
                throw new IllegalStateException("Non-existent enum passed to getPresentationElement() method!");
        }
    }

    private static enum FieldTypeEnum {
        BOOLEAN,
        ENUM,
        GEO_POINT,
        LOCAL_DATE,
        NARRATIVE,
        QUANTITY,
        REFERENCE,
        TEXT;

        private static FieldTypeEnum fromFieldType(FieldType fieldType) {
            if (fieldType instanceof BooleanType) return BOOLEAN;
            if (fieldType instanceof EnumType) return ENUM;
            if (fieldType instanceof GeoPointType) return GEO_POINT;
            if (fieldType instanceof LocalDateType) return LOCAL_DATE;
            if (fieldType instanceof NarrativeType) return NARRATIVE;
            if (fieldType instanceof QuantityType) return QUANTITY;
            if (fieldType instanceof ReferenceType) return REFERENCE;
            if (fieldType instanceof TextType) return TEXT;
            return null;
        }
    }
}
