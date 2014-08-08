package org.activityinfo.server.endpoint.odk;

import com.google.inject.Inject;
import org.activityinfo.model.table.TableService;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateType;

public class OdkFieldValueParserFactory {
    final private TableService table;

    @Inject
    public OdkFieldValueParserFactory(TableService table) {
        this.table = table;
    }

    public OdkFieldValueParser fromFieldType(FieldType fieldType) {
        if (fieldType instanceof BooleanType) return new BooleanFieldValueParser();
        if (fieldType instanceof EnumType) return new EnumFieldValueParser((EnumType) fieldType);
        if (fieldType instanceof GeoPointType) return new GeoPointFieldValueParser();
        if (fieldType instanceof LocalDateType) return new LocalDateFieldValueParser();
        if (fieldType instanceof NarrativeType) return new NarrativeFieldValueParser();
        if (fieldType instanceof QuantityType) return new QuantityFieldValueParser((QuantityType) fieldType);
        if (fieldType instanceof ReferenceType) return new ReferenceFieldValueParser();
        if (fieldType instanceof TextType) return new TextFieldValueParser();

        // If this happens, it means this class needs to be expanded to support the new FieldType class.
        throw new IllegalArgumentException("Unknown FieldType passed to OdkFieldValueParserFactory.fromFieldType()");
    }
}
