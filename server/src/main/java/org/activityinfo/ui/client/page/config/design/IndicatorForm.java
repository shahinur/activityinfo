package org.activityinfo.ui.client.page.config.design;

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

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.core.shared.form.FormFieldTypeLables;
import org.activityinfo.i18n.shared.UiConstants;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.ui.client.widget.legacy.MappingComboBox;
import org.activityinfo.ui.client.widget.legacy.MappingComboBoxBinding;

class IndicatorForm extends AbstractDesignForm {

    private FormBinding binding;

    public IndicatorForm() {
        super();

        binding = new FormBinding(this);

        UiConstants constants = GWT.create(UiConstants.class);

        this.setLabelWidth(150);
        this.setFieldWidth(200);

        NumberField idField = new NumberField();
        idField.setFieldLabel("ID");
        idField.setReadOnly(true);
        binding.addFieldBinding(new FieldBinding(idField, "id"));
        add(idField);

        TextField<String> nameField = new TextField<String>();
        nameField.setFieldLabel(constants.name());
        nameField.setAllowBlank(false);
        binding.addFieldBinding(new FieldBinding(nameField, "name"));
        this.add(nameField);

        MappingComboBox<FormFieldType> typeCombo = new MappingComboBox<>();
        typeCombo.setFieldLabel(constants.type());
        typeCombo.add(FormFieldType.QUANTITY, FormFieldTypeLables.getFormFieldType(FormFieldType.QUANTITY));
        typeCombo.add(FormFieldType.FREE_TEXT, FormFieldTypeLables.getFormFieldType(FormFieldType.FREE_TEXT));
        typeCombo.add(FormFieldType.NARRATIVE, FormFieldTypeLables.getFormFieldType(FormFieldType.NARRATIVE));
//        typeCombo.add(FormFieldType.LOCAL_DATE, FormFieldTypeLables.getFormFieldType(FormFieldType.LOCAL_DATE));
        binding.addFieldBinding(new MappingComboBoxBinding(typeCombo, "type"));
        this.add(typeCombo);

        TextField<String> categoryField = new TextField<String>();
        categoryField.setName("category");
        categoryField.setFieldLabel(constants.category());
        categoryField.setMaxLength(IndicatorDTO.MAX_CATEGORY_LENGTH);
        binding.addFieldBinding(new FieldBinding(categoryField, "category"));
        this.add(categoryField);

        final TextField<String> unitsField = new TextField<String>();
        unitsField.setName("units");
        unitsField.setFieldLabel(constants.units());
        unitsField.setAllowBlank(false);
        unitsField.setMaxLength(IndicatorDTO.UNITS_MAX_LENGTH);
        binding.addFieldBinding(new FieldBinding(unitsField, "units"));
        this.add(unitsField);

        final MappingComboBox aggregationCombo = new MappingComboBox();
        aggregationCombo.setFieldLabel(constants.aggregationMethod());
        aggregationCombo.add(IndicatorDTO.AGGREGATE_SUM, constants.sum());
        aggregationCombo.add(IndicatorDTO.AGGREGATE_AVG, constants.average());
        aggregationCombo.add(IndicatorDTO.AGGREGATE_SITE_COUNT, constants.siteCount());
        binding.addFieldBinding(new MappingComboBoxBinding(aggregationCombo, "aggregation"));
        this.add(aggregationCombo);

        typeCombo.addSelectionChangedListener(new SelectionChangedListener<MappingComboBox.Wrapper<FormFieldType>>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<MappingComboBox.Wrapper<FormFieldType>> wrapperSelectionChangedEvent) {
                if (wrapperSelectionChangedEvent.getSelectedItem() != null) {
                    FormFieldType selectedType = wrapperSelectionChangedEvent.getSelectedItem().getWrappedValue();

                    unitsField.setVisible(selectedType == FormFieldType.QUANTITY);
                    unitsField.setAllowBlank(selectedType != FormFieldType.QUANTITY);
                    if (selectedType != FormFieldType.QUANTITY) {
                        unitsField.setValue("");
                    }

                    aggregationCombo.setVisible(selectedType == FormFieldType.QUANTITY);
                }
            }
        });


        TextField<String> listHeaderField = new TextField<String>();
        listHeaderField.setFieldLabel(constants.listHeader());
        listHeaderField.setMaxLength(IndicatorDTO.MAX_LIST_HEADER_LENGTH);
        binding.addFieldBinding(new FieldBinding(listHeaderField, "listHeader"));
        this.add(listHeaderField);

        TextArea descField = new TextArea();
        descField.setFieldLabel(constants.description());
        binding.addFieldBinding(new FieldBinding(descField, "description"));
        this.add(descField);

        CheckBox mandatoryCB = new CheckBox();
        mandatoryCB.setFieldLabel(constants.mandatory());
        binding.addFieldBinding(new FieldBinding(mandatoryCB, "mandatory"));
        this.add(mandatoryCB);

        hideFieldWhenNull(idField);

    }

    @Override
    public FormBinding getBinding() {
        return binding;
    }
}
