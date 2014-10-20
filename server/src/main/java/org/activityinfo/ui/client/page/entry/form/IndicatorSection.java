package org.activityinfo.ui.client.page.entry.form;

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

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.activityinfo.legacy.client.type.IndicatorNumberFormat;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;
import org.activityinfo.ui.client.page.entry.form.field.AttributeCheckBoxGroup;
import org.activityinfo.ui.client.page.entry.form.field.AttributeCombo;
import org.activityinfo.ui.client.page.entry.form.field.AttributeField;
import org.activityinfo.ui.client.page.entry.form.resources.SiteFormResources;

import java.util.List;

public class IndicatorSection extends LayoutContainer implements FormSection<SiteDTO> {

    public static final int NUMBER_FIELD_WIDTH = 150;
    public static final int TEXT_FIELD_WIDTH = 3 * NUMBER_FIELD_WIDTH;

    private List<Field> indicatorFields = Lists.newArrayList();

    private List<AttributeField> attributeFields = Lists.newArrayList();
    private ActivityDTO activity;


    public IndicatorSection(ActivityDTO activity) {
        this.activity = activity;

        setLayout(new FlowLayout());
        setScrollMode(Scroll.AUTOY);
        addStyleName(SiteFormResources.INSTANCE.style().fieldContainer());

        for (IsFormField field : activity.getFields()) {

            FieldGroup fieldGroup = new FieldGroup();

            if(field instanceof AttributeGroupDTO) {
                AttributeField attributeField = createAttributeWidget((AttributeGroupDTO) field);
                fieldGroup.add(createFieldLabel(field));
                fieldGroup.add((Component) attributeField);
                attributeFields.add(attributeField);

            } else {
                Field fieldWidget = createFieldWidget(field);
                if (fieldWidget != null) {
                    fieldWidget.setName(field.getFieldId());
                    fieldGroup.add(createFieldLabel(field));
                    fieldGroup.add(fieldWidget);
                    if(field.getDescription() != null) {
                        fieldGroup.add(createHelpText(field));
                    }
                    indicatorFields.add(fieldWidget);
                }
            }

            add(fieldGroup);
        }
    }

    private Component createHelpText(IsFormField field) {
        Text text = new Text(field.getDescription());
        text.addStyleName(SiteFormResources.INSTANCE.style().helpText());
        return text;
    }

    private Text createFieldLabel(IsFormField indicator) {
        String label = indicator.getLabel();
        label += createUnitsLabel(indicator);
        if (indicator.isRequired()) {
            label += " *";
        }

        Text labelComponent = new Text(label);
        labelComponent.addStyleName(SiteFormResources.INSTANCE.style().fieldLabel());
        return labelComponent;
    }


    private Field createFieldWidget(IsFormField field) {

        FieldTypeClass type = field.getTypeClass();
        if (type == NarrativeType.TYPE_CLASS) {
            TextArea textArea = new TextArea();
            textArea.setAllowBlank(!field.isRequired());
            textArea.addStyleName(SiteFormResources.INSTANCE.style().fieldControl());
            return textArea;

        } else if (type == QuantityType.TYPE_CLASS) {
            NumberField numberField = new NumberField();
            numberField.setFormat(IndicatorNumberFormat.INSTANCE);
            numberField.setAllowBlank(!field.isRequired());
            numberField.addStyleName(SiteFormResources.INSTANCE.style().fieldControl());
            return numberField;

        } else if (type == TextType.TYPE_CLASS ||
                type == BarcodeType.TYPE_CLASS) {
            TextField textField = new TextField();
            textField.setAllowBlank(!field.isRequired());
            textField.addStyleName(SiteFormResources.INSTANCE.style().fieldControl());
            return textField;

        } else {
            return null;
        }
    }

    private String createUnitsLabel(IsFormField field) {

        if(field.getTypeClass() == QuantityType.TYPE_CLASS) {
            IndicatorDTO indicator = (IndicatorDTO) field;
            if(!Strings.isNullOrEmpty(indicator.getUnits())) {
                return " (" + indicator.getUnits() + ")";
            }
        }
        return "";
    }


    private AttributeField createAttributeWidget(AttributeGroupDTO attributeGroup) {
        AttributeField field;

        if (attributeGroup.isMultipleAllowed()) {

            AttributeCheckBoxGroup boxGroup = new AttributeCheckBoxGroup(attributeGroup);
            boxGroup.setStyleAttribute("marginBottom", "10px");
            boxGroup.setStyleAttribute("width", "100%"); // if the width is specified in px, IE6 flips out
            add(boxGroup);
            field = boxGroup;

        } else {
            AttributeCombo combo = new AttributeCombo(attributeGroup);
            add(combo);
            field = combo;
        }

        field.setReadOnly(attributeGroup.isWorkflow() && !activity.getDatabase().isDesignAllowed());

        return field;
    }

    @Override
    public boolean validate() {
        boolean valid = true;
        for (Field field : indicatorFields) {
            valid &= field.validate();
        }
        for (AttributeField field : attributeFields) {
            valid &= field.validate();
        }
        return valid;
    }

    @Override
    public void updateModel(SiteDTO m) {
        for (Field field : indicatorFields) {
            m.set(field.getName(), field.getValue());
        }
        for (AttributeField field : attributeFields) {
            field.updateModel(m);
        }
    }

    @Override
    public void updateForm(SiteDTO m, boolean isNew) {
        for (Field field : indicatorFields) {
            field.setValue(m.get(field.getName()));
        }
        for (AttributeField field : attributeFields) {
            field.updateForm(m, isNew);
        }
    }

    @Override
    public Component asComponent() {
        return this;
    }


}
