package org.activityinfo.server.endpoint.odk;

import com.google.inject.Inject;
import org.activityinfo.model.table.TableService;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ParametrizedFieldType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateType;

public class OdkFormFieldBuilderFactory {
    final private TableService table;

    @Inject
    public OdkFormFieldBuilderFactory(TableService table) {
        this.table = table;
    }

    public OdkFormFieldBuilder fromFieldType(FieldType fieldType) {
        if (fieldType instanceof ParametrizedFieldType) {
            ParametrizedFieldType parametrizedFieldType = (ParametrizedFieldType) fieldType;
            if (!parametrizedFieldType.isValid()) return null;
        }

        SelectOptions selectOptions = getSelectOptions(fieldType);

        if (fieldType instanceof BarcodeType) return new SimpleInputBuilder("barcode");
        if (fieldType instanceof BooleanType) return new SelectBuilder("boolean", selectOptions);
        if (fieldType instanceof EnumType) return new SelectBuilder("string", selectOptions);
        if (fieldType instanceof GeoPointType) return new SimpleInputBuilder("geopoint");
        if (fieldType instanceof LocalDateType) return new SimpleInputBuilder("date");
        if (fieldType instanceof NarrativeType) return new SimpleInputBuilder("string");
        if (fieldType instanceof QuantityType) return new QuantityFieldBuilder((QuantityType) fieldType);
        if (fieldType instanceof ReferenceType) return new SelectBuilder("string", selectOptions);
        if (fieldType instanceof TextType) return new SimpleInputBuilder("string");

        // If this happens, it means this class needs to be expanded to support the new FieldType class.
        throw new IllegalArgumentException("Unknown FieldType passed to OdkFormFieldBuilderFactory.fromFieldType()");
    }

    private SelectOptions getSelectOptions(FieldType fieldType) {
        if (fieldType instanceof BooleanType) return new BooleanTypeSelectOptions();
        if (fieldType instanceof EnumType) return new EnumTypeSelectOptions((EnumType) fieldType);
        if (fieldType instanceof ReferenceType) return new ReferenceTypeSelectOptions((ReferenceType) fieldType, table);
        return null;
    }
}
