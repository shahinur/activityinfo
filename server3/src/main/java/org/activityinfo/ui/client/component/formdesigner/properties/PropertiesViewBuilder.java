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

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.ui.client.component.formdesigner.container.FieldWidgetContainer;

import java.util.List;

/**
 * @author yuriyz on 7/9/14.
 */
public class PropertiesViewBuilder {

    private FieldWidgetContainer widgetContainer;

    public PropertiesViewBuilder(FieldWidgetContainer widgetContainer) {
        this.widgetContainer = widgetContainer;
    }

    public List<PropertyTypeView> build() {
        FieldTypeClass typeClass = widgetContainer.getFormField().getType().getTypeClass();
        final List<PropertyTypeView> result = Lists.newArrayList();
        if (typeClass != null && typeClass.getParameterFormClass() != null) { // for some types it can be null
            for (FormField formField : typeClass.getParameterFormClass().getFields()) {
                result.add(create(formField));
            }
        }
        return result;
    }

    private PropertyTypeView create(FormField formField) {
        return new PropertyTypeViewPanel(widgetContainer, formField);
    }
}
