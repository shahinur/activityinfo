package org.activityinfo.service.tasks.appengine.export;

import com.google.common.collect.Lists;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.expr.eval.FieldReader;
import org.activityinfo.model.expr.eval.PartialEvaluator;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.type.*;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.barcode.BarcodeValue;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.expr.ExprFieldType;
import org.activityinfo.model.type.geo.GeoPoint;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.image.ImageRowValue;
import org.activityinfo.model.type.image.ImageType;
import org.activityinfo.model.type.image.ImageValue;
import org.activityinfo.model.type.number.Quantity;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanFieldValue;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.model.type.time.*;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

class ColumnListBuilder implements FormClassVisitor<List<Column>> {


    public List<FieldColumnSet> build(FormClass formClass) {

        PartialEvaluator evaluator = new PartialEvaluator(formClass);

        List<FieldColumnSet> bindings = Lists.newArrayList();
        for(FormField field : formClass.getFields()) {
            FieldReader reader = evaluator.partiallyEvaluate(field);
            FieldType fieldType = reader.getType();
            bindings.add(new FieldColumnSet(field, reader, getColumns(field, fieldType)));
        }
        return bindings;
    }

    private List<Column> getColumns(FormField field, FieldType fieldType) {
        return fieldType.accept(field, this);
    }

    @Override
    public List<Column> visitTextField(FormField field, TextType type) {
        return Column.unique(new FieldValueConverter<TextValue>() {

            @Override
            public Object convertValue(@Nonnull TextValue fieldValue) {
                return fieldValue.asString();
            }
        });
    }

    @Override
    public List<Column> visitQuantityField(FormField field, QuantityType type) {
        return Column.unique(new FieldValueConverter<Quantity>() {
            @Override
            public Object convertValue(@Nonnull Quantity fieldValue) {
                return fieldValue.getValue();
            }
        });
    }

    @Override
    public List<Column> visitReferenceField(FormField field, ReferenceType type) {
        return Column.unique(new FieldValueConverter<ReferenceValue>() {

            @Override
            public Object convertValue(@Nonnull ReferenceValue fieldValue) {
                return fieldValue.getResourceId().asString();
            }
        });
    }

    @Override
    public List<Column> visitEnumField(FormField field, EnumType type) {
        if(type.getCardinality() == Cardinality.SINGLE || type.getValues().size() <= 1) {
            return Column.unique(new UniqueEnumValueConverter(field, type));
        } else {
            List<Column> columns = Lists.newArrayList();
            for(EnumValue enumValue : type.getValues()) {
                columns.add(new Column(enumValue.getLabel(), new BinaryEnumConverter(enumValue)));
            }
            return columns;
        }
    }

    @Override
    public List<Column> visitBarcodeField(FormField field, BarcodeType type) {
        return Column.unique(new FieldValueConverter<BarcodeValue>() {

            @Override
            public Object convertValue(@Nonnull BarcodeValue fieldValue) {
                return fieldValue.asString();
            }
        });
    }

    @Override
    public List<Column> visitBooleanField(FormField field, BooleanType type) {
        return Column.unique(new FieldValueConverter<BooleanFieldValue>() {

            @Override
            public Object convertValue(@Nonnull BooleanFieldValue fieldValue) {
                return fieldValue.asBoolean();
            }
        });
    }

    @Override
    public List<Column> visitCalculatedField(FormField field, CalculatedFieldType type) {
        throw new IllegalStateException();
    }

    @Override
    public List<Column> visitGeoPointField(FormField field, GeoPointType type) {
        return Arrays.asList(
            new Column(I18N.CONSTANTS.latitude(), new FieldValueConverter<GeoPoint>() {

                @Override
                public Object convertValue(@Nonnull GeoPoint fieldValue) {
                    return fieldValue.getLatitude();
                }
            }),
            new Column(I18N.CONSTANTS.longitude(), new FieldValueConverter<GeoPoint>() {

                @Override
                public Object convertValue(@Nonnull GeoPoint fieldValue) {
                    return fieldValue.getLongitude();
                }
            })
        );
    }

    @Override
    public List<Column> visitImageField(FormField field, ImageType type) {
        return Column.unique(new FieldValueConverter<ImageValue>() {
            @Override
            public Object convertValue(@Nonnull ImageValue fieldValue) {
                if(fieldValue.getValues().size() >= 1) {
                    ImageRowValue value = fieldValue.getValues().get(0);
                    return value.getFilename();
                } else {
                    return null;
                }
            }
        });
    }

    @Override
    public List<Column> visitExprField(FormField field, ExprFieldType type) {
        return Arrays.asList();
    }

    @Override
    public List<Column> visitLocalDateField(FormField field, LocalDateType localDateType) {
        return Column.unique(new FieldValueConverter<LocalDate>() {

            @Override
            public Object convertValue(@Nonnull LocalDate fieldValue) {
                return fieldValue;
            }
        });
    }

    @Override
    public List<Column> visitNarrativeField(FormField field, NarrativeType narrativeType) {
        return Column.unique(new FieldValueConverter<NarrativeValue>() {

            @Override
            public Object convertValue(@Nonnull NarrativeValue fieldValue) {
                return fieldValue.asString();
            }
        });
    }

    @Override
    public List<Column> visitMonthField(FormField field, MonthType monthType) {
        return Column.unique(new FieldValueConverter<MonthValue>() {

            @Override
            public Object convertValue(@Nonnull MonthValue fieldValue) {
                return fieldValue.toString();
            }
        });
    }

    @Override
    public List<Column> visitYearField(FormField field, YearType yearType) {
        return Column.unique(new FieldValueConverter<YearValue>() {

            @Override
            public Object convertValue(@Nonnull YearValue fieldValue) {
                return fieldValue.getYear();
            }
        });
    }

    @Override
    public List<Column> visitMissingField(FormField field, MissingFieldType missingFieldType) {
        throw new IllegalStateException();
    }

    @Override
    public List<Column> visitSubForm(FormField field, RecordFieldType fieldType) {
        if(fieldType.getClassId().equals(LocalDateIntervalClass.CLASS_ID)) {
            return Arrays.asList(
                    new Column(I18N.CONSTANTS.startDate(), new FieldValueConverter<Record>() {

                        @Override
                        public Object convertValue(@Nonnull Record fieldValue) {
                            return Types.read(fieldValue, LocalDateIntervalClass.START_DATE_FIELD_NAME, LocalDateType.TYPE_CLASS);
                        }
                    }),
                    new Column(I18N.CONSTANTS.endDate(), new FieldValueConverter<Record>() {

                        @Override
                        public Object convertValue(@Nonnull Record fieldValue) {
                            return Types.read(fieldValue,  LocalDateIntervalClass.END_DATE_FIELD_NAME, LocalDateType.TYPE_CLASS);
                        }
                    }));
        } else {
            return null;
        }
     }
}
