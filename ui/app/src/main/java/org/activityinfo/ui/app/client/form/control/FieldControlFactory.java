package org.activityinfo.ui.app.client.form.control;

import org.activityinfo.model.form.FormClassVisitor;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.expr.ExprFieldType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.image.ImageType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateIntervalType;
import org.activityinfo.model.type.time.LocalDateType;
import org.activityinfo.model.type.time.MonthType;
import org.activityinfo.model.type.time.YearType;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.vdom.shared.tree.VComponent;

public class FieldControlFactory implements FormClassVisitor<VComponent> {

    private final Dispatcher dispatcher;

    public FieldControlFactory(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public VComponent create(FormField field) {
        return field.accept(this);
    }

    @Override
    public VComponent visitTextField(FormField field, TextType type) {
        return new TextControl(dispatcher, field);
    }

    @Override
    public VComponent visitQuantityField(FormField field, QuantityType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitReferenceField(FormField field, ReferenceType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitEnumField(FormField field, EnumType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitBarcodeField(FormField field, BarcodeType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitBooleanField(FormField field, BooleanType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitCalculatedField(FormField field, CalculatedFieldType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitGeoPointField(FormField field, GeoPointType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitImageField(FormField field, ImageType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitLocalDateIntervalField(FormField field, LocalDateIntervalType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitExprField(FormField field, ExprFieldType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitLocalDateField(FormField field, LocalDateType localDateType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitNarrativeField(FormField field, NarrativeType narrativeType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitMonthField(FormField field, MonthType monthType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VComponent visitYearField(FormField field, YearType yearType) {
        throw new UnsupportedOperationException();
    }
}
