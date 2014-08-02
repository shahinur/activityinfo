package org.activityinfo.ui.client.component.formdesigner.skip;
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

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;
import org.activityinfo.core.shared.expr.ExprFunction;
import org.activityinfo.core.shared.expr.functions.BooleanFunctions;
import org.activityinfo.core.shared.expr.functions.FieldTypeToFunctionRegistry;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;
import org.activityinfo.ui.client.component.form.field.FormFieldWidgetFactory;
import org.activityinfo.ui.client.component.formdesigner.container.FieldWidgetContainer;

import java.util.List;

/**
 * @author yuriyz on 7/24/14.
 */
public class SkipRowPresenter {

    private final FieldWidgetContainer fieldWidgetContainer;
    private final SkipRow view = new SkipRow();
    private final FormFieldWidgetFactory widgetFactory;
    private FormFieldWidget valueWidget = null;
    private FieldValue value;
    private RowData rowData;

    public SkipRowPresenter(final FieldWidgetContainer fieldWidgetContainer) {
        this.fieldWidgetContainer = fieldWidgetContainer;
        this.widgetFactory = new FormFieldWidgetFactory(fieldWidgetContainer.getFormDesigner().getResourceLocator());

        initFormFieldBox();
        initFunction();
        initValueWidget();
        initJoinFunction();
    }

    // depends on selected field type
    private void initValueWidget() {
        view.getValueContainer().clear();

        ValueUpdater<FieldValue> valueUpdater = new ValueUpdater<FieldValue>() {
            @Override
            public void update(FieldValue value) {
                SkipRowPresenter.this.value = value;
            }
        };

        widgetFactory.createWidget(getSelectedFormField(), valueUpdater).then(new AsyncCallback<FormFieldWidget>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(FormFieldWidget widget) {
                valueWidget = widget;
                view.getValueContainer().add(widget);

                if (rowData != null) {
                    valueWidget.setValue(rowData.getValue());
                    SkipRowPresenter.this.value = rowData.getValue();
                }
            }
        });
    }

    private void initFormFieldBox() {
        view.getFormfield().clear();

        List<FormField> formFields = Lists.newArrayList(fieldWidgetContainer.getFormDesigner().getFormClass().getFields());
        formFields.remove(fieldWidgetContainer.getFormField()); // remove selected field

        for (FormField formField :  formFields) {
            view.getFormfield().addItem(formField.getLabel(), formField.getId().asString());
        }
        view.getFormfield().addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                initFunction();
                initValueWidget();
            }
        });
    }

    public FormField getSelectedFormField() {
        String formFieldId = view.getFormfield().getValue(view.getFormfield().getSelectedIndex());
        return fieldWidgetContainer.getFormDesigner().getFormClass().getField(ResourceId.create(formFieldId));
    }

    // depends on selected field type
    private void initFunction() {
        view.getFunction().clear();

        List<ExprFunction> functions = FieldTypeToFunctionRegistry.get().getFunctions(getSelectedFormField().getType().getTypeClass());
        for (ExprFunction function : functions) {
            view.getFunction().addItem(function.getLabel(), function.getId());
        }
    }

    private void initJoinFunction() {
        view.getJoinFunction().addItem(BooleanFunctions.AND.getLabel(), BooleanFunctions.AND.getId());
        view.getJoinFunction().addItem(BooleanFunctions.OR.getLabel(), BooleanFunctions.OR.getId());
        view.getJoinFunction().setSelectedIndex(0);
    }

    public SkipRow getView() {
        return view;
    }

    public FieldValue getValue() {
        return value;
    }

    public void updateWith(final RowData rowData) {
        this.rowData = rowData;
        setSelectedValue(view.getJoinFunction(), rowData.getJoinFunction().getId());
        setSelectedValue(view.getFunction(), rowData.getFunction().getId());
        setSelectedValue(view.getFormfield(), rowData.getFormField().getId().asString());
        initValueWidget();
    }

    private static void setSelectedValue(ListBox listBox, String value) {
        for (int i = 0; i< listBox.getItemCount(); i++) {
            String itemValue = listBox.getValue(i);
            if (!Strings.isNullOrEmpty(itemValue) && itemValue.equals(value)) {
                listBox.setSelectedIndex(i);
                return;
            }
        }
    }
}
