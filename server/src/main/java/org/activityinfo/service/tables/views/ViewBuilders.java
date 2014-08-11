package org.activityinfo.service.tables.views;

import com.google.common.base.Optional;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.table.ColumnType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.service.tables.reader.QuantityDoubleReader;
import org.activityinfo.service.tables.reader.TextFieldStringReader;

public class ViewBuilders {


    /**
     * Creates a builder for the given FormField if the {@code columnType} is compatible.
     */
    public static Optional<ColumnViewBuilder> createBuilder(FormField field, ColumnType type) {
        switch(type) {
            case STRING:
                return Optional.fromNullable(createStringBuilder(field));
            case NUMBER:
                return Optional.fromNullable(createDoubleBuilder(field));
            case DATE:
                break;
        }
        throw new IllegalArgumentException("type: " + type);
    }

    private static ColumnViewBuilder createStringBuilder(FormField field) {
        if(field.getType() instanceof TextType) {
            return new StringColumnBuilder(new TextFieldStringReader(field.getId().asString()));

        } else if(field.getType() instanceof EnumType) {
            return new EnumColumnBuilder(field.getId(), (EnumType) field.getType());

        } else {
            return null;
        }
    }

    private static ColumnViewBuilder createDoubleBuilder(FormField field) {
        if(field.getType() instanceof QuantityType) {
            return new DoubleColumnBuilder(new QuantityDoubleReader(field.getId()));
        } else {
            return null;
        }
    }
}
