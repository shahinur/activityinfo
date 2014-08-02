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
import com.google.gwt.cell.client.ValueUpdater;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.BooleanType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.model.type.time.LocalDateIntervalType;
import org.activityinfo.model.type.time.LocalDateType;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.client.component.form.field.hierarchy.HierarchyFieldWidget;

import java.util.List;

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

    public Promise<? extends FormFieldWidget> createWidget(FormField field, ValueUpdater valueUpdater) {
        FieldType type = field.getType();

        if (type instanceof QuantityType) {
            return Promise.resolved(new QuantityFieldWidget((QuantityType) type, valueUpdater));

        } else if (type instanceof NarrativeType) {
            return Promise.resolved(new NarrativeFieldWidget(valueUpdater));

        } else if (type instanceof TextType) {
            return Promise.resolved(new TextFieldWidget(valueUpdater));

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

        } else if (type instanceof ReferenceType) {
            if (field.isSubPropertyOf(ApplicationProperties.HIERARCHIAL)) {
                return HierarchyFieldWidget.create(resourceLocator, (ReferenceType) type, valueUpdater);
            }
            return createReferenceWidget(field, valueUpdater);
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
        return resourceLocator
                .queryInstances(type.getRange())
                .then(new Function<List<FormInstance>, FormFieldWidget>() {
                    @Override
                    public FormFieldWidget apply(List<FormInstance> input) {

                        if (input.size() < SMALL_BALANCE_NUMBER) {
                            // Radio buttons
                            return new CheckBoxFieldWidget(type, input, valueUpdater);

                        } else if (input.size() < MEDIUM_BALANCE_NUMBER) {
                            // Dropdown list
                            return new ComboBoxFieldWidget(input, valueUpdater);

                        } else {
                            // Suggest box
                            return new SuggestBoxWidget(input, valueUpdater);
                        }
                    }
                });
    }
}

