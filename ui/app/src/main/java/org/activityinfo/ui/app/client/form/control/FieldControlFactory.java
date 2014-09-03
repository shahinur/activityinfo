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
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.vdom.shared.tree.VThunk;

public class FieldControlFactory implements FormClassVisitor<VThunk> {

    private final Dispatcher dispatcher;

    public FieldControlFactory(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public VThunk create(FormField field) {
        return field.accept(this);
    }

    @Override
    public VThunk visitTextField(FormField field, TextType type) {
        return new TextControl(dispatcher, field);
    }

    @Override
    public VThunk visitQuantityField(FormField field, QuantityType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VThunk visitReferenceField(FormField field, ReferenceType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VThunk visitEnumField(FormField field, EnumType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VThunk visitBarcodeField(FormField field, BarcodeType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VThunk visitBooleanField(FormField field, BooleanType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VThunk visitCalculatedField(FormField field, CalculatedFieldType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VThunk visitGeoPointField(FormField field, GeoPointType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VThunk visitImageField(FormField field, ImageType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VThunk visitLocalDateIntervalField(FormField field, LocalDateIntervalType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VThunk visitExprField(FormField field, ExprFieldType type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VThunk visitLocalDateField(FormField field, LocalDateType localDateType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public VThunk visitNarrativeField(FormField field, NarrativeType narrativeType) {
        throw new UnsupportedOperationException();
    }
}
