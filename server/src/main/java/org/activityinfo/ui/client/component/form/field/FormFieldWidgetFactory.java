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

import com.google.gwt.cell.client.ValueUpdater;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.TextType;
import org.activityinfo.model.type.geo.GeoPointType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.time.LocalDateType;
import org.activityinfo.ui.client.component.form.field.hierarchy.HierarchyFieldWidget;
import org.activityinfo.ui.client.component.form.model.FieldViewModel;
import org.activityinfo.ui.client.component.form.model.FormViewModel;
import org.activityinfo.ui.client.component.form.model.HierarchyViewModel;
import org.activityinfo.ui.client.component.form.model.SimpleListViewModel;

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

    public FormFieldWidget createWidget(FormViewModel viewModel, FormTree.Node node, ValueUpdater valueUpdater) {
        FieldType type = node.getType();
        if(type instanceof QuantityType) {
            return new QuantityFieldWidget((QuantityType) type, valueUpdater);

        } else if(type instanceof NarrativeType) {
            return new NarrativeFieldWidget(valueUpdater);

        } else if(type instanceof TextType) {
            return new TextFieldWidget(valueUpdater);

        } else if(type instanceof LocalDateType) {
            return new DateFieldWidget(valueUpdater);

        } else if(type instanceof GeoPointType) {
            return new GeographicPointWidget(valueUpdater);

        } else if(type instanceof ReferenceType) {
            return createReferenceWidget(viewModel, node, valueUpdater);
        }

        Log.error("Unexpected field type " + node.getTypeClass());
        throw new UnsupportedOperationException();
    }

    private FormFieldWidget createReferenceWidget(FormViewModel formModel, FormTree.Node node,
                                                  ValueUpdater valueUpdater) {

        FieldViewModel fieldModel = formModel.getFieldViewModel(node.getFieldId());

        if(fieldModel instanceof HierarchyViewModel) {
            return new HierarchyFieldWidget(resourceLocator, (HierarchyViewModel) fieldModel, valueUpdater);

        } else if(fieldModel instanceof SimpleListViewModel) {
            return createSimpleListWidget((SimpleListViewModel)fieldModel, valueUpdater);

        } else {
            Log.error("Unknown fieldModel " + fieldModel.getClass().getSimpleName());
            throw new IllegalArgumentException();
        }
    }

    private FormFieldWidget createSimpleListWidget(SimpleListViewModel fieldModel, ValueUpdater valueUpdater) {

        if (fieldModel.getCount() < SMALL_BALANCE_NUMBER) {
            // Radio buttons
            return new CheckBoxFieldWidget(fieldModel, valueUpdater);

        } else if (fieldModel.getCount() < MEDIUM_BALANCE_NUMBER) {
            // Dropdown list
            return new ComboBoxFieldWidget(fieldModel, valueUpdater);

        } else {
            // Suggest box
            return new SuggestBoxWidget(fieldModel, valueUpdater);
        }
    }
}
