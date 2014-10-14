package org.activityinfo.ui.component.formdesigner.palette;

import com.google.common.collect.Lists;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.RecordFieldType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateIntervalClass;
import org.activityinfo.model.type.time.LocalDateType;

import java.util.List;

public class FieldTemplates {


    public static List<FieldTemplate> list() {
        List<FieldTemplate> items = Lists.newArrayList();

        items.add(new QuantityTemplate());
        items.add(new SimpleTypeTemplate(TextType.INSTANCE, I18N.CONSTANTS.fieldTypeText()));
        items.add(new SimpleTypeTemplate(NarrativeType.INSTANCE, I18N.CONSTANTS.fieldTypeNarrative()));
        items.add(new SimpleTypeTemplate(LocalDateType.INSTANCE, I18N.CONSTANTS.fieldTypeDate()));
        items.add(new SimpleTypeTemplate(new RecordFieldType(LocalDateIntervalClass.CLASS_ID), I18N.CONSTANTS.dateInterval()));

        items.add(new CheckboxTemplate());
        items.add(new RadioButtonTemplate());

        items.add(new ReferenceTemplate());

        items.add(new SimpleTypeTemplate(GeoPointType.INSTANCE, I18N.CONSTANTS.fieldTypeGeographicPoint()));
        items.add(new SimpleTypeTemplate(BarcodeType.INSTANCE, I18N.CONSTANTS.fieldTypeBarCode()));
        items.add(new ImageTemplate());
        items.add(new CalculatedFieldTemplate());

        return items;
    }
}
