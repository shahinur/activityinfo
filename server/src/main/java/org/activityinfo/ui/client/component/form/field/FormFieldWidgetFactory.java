package org.activityinfo.ui.client.component.form.field;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.gwt.cell.client.ValueUpdater;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.system.ApplicationProperties;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.model.type.expr.CalculatedFieldType;
import org.activityinfo.model.type.expr.ExprFieldType;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.image.ImageType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateIntervalType;
import org.activityinfo.model.type.time.LocalDateType;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.component.form.field.hierarchy.HierarchyFieldWidget;
import org.activityinfo.ui.client.component.form.field.image.ImageUploadFieldWidget;

/**
 * @author yuriyz on 1/28/14.
 */
public class FormFieldWidgetFactory {


    /**
     * Based on this numbers FormField Widget generates different widgets and layouts:
     * <p/>
     * 1. Single :
     * less SMALL_BALANCE_NUMBER -> Radio buttons
     * less MEDIUM_BALANCE_NUMBER -> Dropdown list
     * more MEDIUM_BALANCE_NUMBER -> Suggest box
     * 2. Multiple :
     * less SMALL_BALANCE_NUMBER -> Check boxes
     * less MEDIUM_BALANCE_NUMBER -> List of selected + add button
     * more MEDIUM_BALANCE_NUMBER -> List of selected + add button
     */
    public static final int SMALL_BALANCE_NUMBER = 10;
    public static final int MEDIUM_BALANCE_NUMBER = 20;

    private ResourceLocator resourceLocator;

    public FormFieldWidgetFactory(ResourceLocator resourceLocator) {
        this.resourceLocator = resourceLocator;
    }

    public Promise<? extends FormFieldWidget> createWidget(ResourceId formClassId, FormField field, ValueUpdater valueUpdater) {
        FieldType type = field.getType();

        if (type instanceof QuantityType) {
            return Promise.resolved(new QuantityFieldWidget((QuantityType) type, valueUpdater));

        } else if (type instanceof NarrativeType) {
            return Promise.resolved(new NarrativeFieldWidget(valueUpdater));

        } else if (type instanceof TextType) {
            return Promise.resolved(new TextFieldWidget(valueUpdater));

        } else if (type instanceof ExprFieldType) {
            return Promise.resolved(new ExprFieldWidget(valueUpdater));

        } else if (type instanceof CalculatedFieldType) {
            return Promise.resolved(new CalculatedFieldWidget(valueUpdater));

        } else if (type instanceof LocalDateType) {
            return Promise.resolved(new DateFieldWidget(valueUpdater));

        } else if (type instanceof LocalDateIntervalType) {
            return Promise.resolved(new DateIntervalFieldWidget(valueUpdater));

        } else if (type instanceof GeoPointType) {
            return Promise.resolved(new GeographicPointWidget(valueUpdater));

        } else if (type instanceof EnumType) {
            return Promise.resolved(new EnumFieldWidget((EnumType) field.getType(), valueUpdater));

        } else if (type instanceof BooleanType) {
            return Promise.resolved(new BooleanFieldWidget(valueUpdater));

        }  else if (type instanceof ImageType) {
            return Promise.resolved(new ImageUploadFieldWidget(field, valueUpdater));

        } else if (type instanceof ReferenceType) {
            if (field.isSubPropertyOf(ApplicationProperties.HIERARCHIAL)) {
                return HierarchyFieldWidget.create(resourceLocator, (ReferenceType) type, valueUpdater);
            }
            return createReferenceWidget(field, valueUpdater);
        } else if (type instanceof BarcodeType) {
            return Promise.resolved(new BarcodeFieldWidget(valueUpdater));
        }

        Log.error("Unexpected field type " + type.getTypeClass());
        throw new UnsupportedOperationException();
    }

    private Promise<? extends FormFieldWidget> createReferenceWidget(FormField field, ValueUpdater updater) {
        if (field.isSubPropertyOf(ApplicationProperties.HIERARCHIAL)) {
            return HierarchyFieldWidget.create(resourceLocator, (ReferenceType) field.getType(), updater);
        } else {
            return createSimpleListWidget((ReferenceType) field.getType(), updater);
        }
    }

    private Promise createSimpleListWidget(final ReferenceType type, final ValueUpdater valueUpdater) {

        TableModel tableModel = new TableModel(Iterables.getOnlyElement(type.getRange()));
        tableModel.addResourceId("id");
        tableModel.addColumn("label").select().fieldPath(ApplicationProperties.LABEL_PROPERTY);

        return resourceLocator.queryTable(tableModel).then(new Function<TableData, FormFieldWidget>() {
            @Override
            public FormFieldWidget apply(TableData table) {

                InstanceLabelTable instanceLabelTable = new InstanceLabelTable(table.getColumnView("id"),
                        table.getColumnView("label"));

                if (table.getNumRows() < SMALL_BALANCE_NUMBER) {
                    // Radio buttons
                    return new CheckBoxFieldWidget(type, instanceLabelTable, valueUpdater);

                } else if (table.getNumRows() < MEDIUM_BALANCE_NUMBER) {
                    // Dropdown list
                    return new ComboBoxFieldWidget(instanceLabelTable, valueUpdater);

                } else {
                    // Suggest box
                    return new SuggestBoxWidget(instanceLabelTable, valueUpdater);
                }
            }
        });
    }
}

