package org.activityinfo.ui.client.component.formdesigner.properties;
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.ui.client.component.formdesigner.container.FieldWidgetContainer;
import org.activityinfo.ui.client.widget.TextBox;

/**
 * @author yuriyz on 7/10/14.
 */
public class PropertyTypeViewPanel extends Composite implements PropertyTypeView {

    private static OurUiBinder uiBinder = GWT
            .create(OurUiBinder.class);

    interface OurUiBinder extends UiBinder<Widget, PropertyTypeViewPanel> {
    }

    private final FieldWidgetContainer widgetContainer;
    private final FormField formField;
    @UiField
    TextBox label;
    @UiField
    Label name;
    @UiField
    SpanElement errorMessage;
    @UiField
    HTML errorMessageContainer;

    public PropertyTypeViewPanel(final FieldWidgetContainer widgetContainer, final FormField formField) {
        this.widgetContainer = widgetContainer;
        this.formField = formField;

        initWidget(uiBinder.createAndBindUi(this));
        syncWithModel();

        label.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (validate()) {
                    widgetContainer.getFormField().getType().getParameters().set(formField.getName(), label.getValue());
                    syncWithModel();
                }
            }
        });
    }

    /**
     * In 'master' branch we are going to have more general Validator for FormField definition encoded directly in type FormClass.
     * Since here we are far behind it's probably enough to "hardcode" validation as it is done below.
     *
     * @return whether value for form field is valid or not.
     */
    private boolean validate() {
        errorMessageContainer.setVisible(false);

        String value = label.getValue();

        ResourceId quantityUnits = QuantityType.TypeClass.INSTANCE.getParameterFormClass().getField(ResourceId.create("_quantity$units")).getId();

        if (formField.getId().equals(quantityUnits)) {
            if (value !=null && value.length() > IndicatorDTO.UNITS_MAX_LENGTH) {
                showError(I18N.MESSAGES.exceedsMaximumLength(IndicatorDTO.UNITS_MAX_LENGTH));
                return false;
            }
        }
        return true;
    }

    private void showError(String error) {
        errorMessageContainer.setVisible(true);
        errorMessage.setInnerText(error);
    }


    @Override
    public FormField getFormField() {
        return formField;
    }

    @Override
    public void syncWithModel() {
        label.setValue(widgetContainer.getFormField().getType().getParameters().getString(formField.getName()));
        name.setText(Strings.nullToEmpty(formField.getName()));
        widgetContainer.syncWithModel();
    }
}
