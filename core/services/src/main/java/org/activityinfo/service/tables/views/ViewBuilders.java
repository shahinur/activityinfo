package org.activityinfo.service.tables.views;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.HasStringValue;
import org.activityinfo.model.type.primitive.TextType;

import javax.annotation.Nullable;

public class ViewBuilders {


    /**
     * Creates a builder for the given FormField if the {@code columnType} is compatible.
     */
    public static Optional<ColumnViewBuilder> createBuilder(ResourceId fieldId, FieldType fieldType,
                                                            ColumnType columnType) {
        switch(columnType) {
            case STRING:
                return Optional.fromNullable(createStringBuilder(fieldId, fieldType));
            case NUMBER:
                return Optional.fromNullable(createDoubleBuilder(fieldId, fieldType));
            case DATE:
                break;
        }
        throw new IllegalArgumentException("type: " + columnType);
    }

    private static ColumnViewBuilder createStringBuilder(ResourceId fieldId, FieldType fieldType) {

        if (fieldType instanceof TextType ||
            fieldType instanceof BarcodeType ||
            fieldType instanceof NarrativeType) {
            return new StringColumnBuilder(fieldId);

        } else if (fieldType instanceof EnumType) {
            return new EnumColumnBuilder(fieldId, (EnumType) fieldType);

        } else if (fieldType instanceof ReferenceType) {
            return new StringColumnBuilder(fieldId);

        } else {
            // hack to make sure we have values for all fields in the table
            return new StringColumnBuilder(fieldId);
        }
    }

    private static ColumnViewBuilder createDoubleBuilder(ResourceId fieldId, FieldType fieldType) {
        if(fieldType instanceof QuantityType) {
            return new DoubleColumnBuilder(fieldId);
        } else {
            return null;
        }
    }

    private static class HasStringValueConverter implements Function<FieldValue, String> {
        @Nullable
        @Override
        public String apply(FieldValue fieldValue) {
            if(fieldValue instanceof HasStringValue) {
                return ((HasStringValue) fieldValue).asString();
            } else {
                return null;
            }
        }
    }

    private static class ReferenceToString implements Function<FieldValue, String> {

        @Nullable
        @Override
        public String apply(FieldValue input) {
            if(input instanceof ReferenceValue) {
                ReferenceValue refValue = (ReferenceValue) input;
                if(refValue.getResourceIds().size() == 1) {
                    return refValue.getResourceId().asString();
                }
            }
            return null;
        }
    }
}
