package org.activityinfo.ui.app.client.page.form;

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
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

public class FieldIconProvider implements FormClassVisitor<Icon> {

    private static final FieldIconProvider INSTANCE = new FieldIconProvider();

    public static Icon get(FormField field) {
        return field.accept(INSTANCE);
    }

    private FieldIconProvider() {
    }

    private static final Icon DEFAULT = FontAwesome.PENCIL_SQUARE_O;

    @Override
    public Icon visitTextField(FormField field, TextType type) {
        return FontAwesome.FONT;
    }

    @Override
    public Icon visitQuantityField(FormField field, QuantityType type) {
        return FontAwesome.SORT_NUMERIC_ASC;
    }

    @Override
    public Icon visitReferenceField(FormField field, ReferenceType type) {
        return FontAwesome.LINK;
    }

    @Override
    public Icon visitEnumField(FormField field, EnumType type) {
        return FontAwesome.LIST_UL;
    }

    @Override
    public Icon visitBarcodeField(FormField field, BarcodeType type) {
        return FontAwesome.BARCODE;
    }

    @Override
    public Icon visitBooleanField(FormField field, BooleanType type) {
        return FontAwesome.CHECK_SQUARE_O;
    }

    @Override
    public Icon visitCalculatedField(FormField field, CalculatedFieldType type) {
        return FontAwesome.SUPERSCRIPT;
    }

    @Override
    public Icon visitGeoPointField(FormField field, GeoPointType type) {
        return FontAwesome.GLOBE;
    }

    @Override
    public Icon visitImageField(FormField field, ImageType type) {
        return FontAwesome.IMAGE;
    }

    @Override
    public Icon visitLocalDateIntervalField(FormField field, LocalDateIntervalType type) {
        return FontAwesome.CALENDAR;
    }

    @Override
    public Icon visitExprField(FormField field, ExprFieldType type) {
        return DEFAULT;
    }

    @Override
    public Icon visitLocalDateField(FormField field, LocalDateType localDateType) {
        return DEFAULT;
    }

    @Override
    public Icon visitNarrativeField(FormField field, NarrativeType narrativeType) {
        return DEFAULT;
    }

    @Override
    public Icon visitMonthField(FormField field, MonthType monthType) {
        return FontAwesome.CALENDAR;
    }

    @Override
    public Icon visitYearField(FormField field, YearType yearType) {
        return FontAwesome.CALENDAR;
    }
}
