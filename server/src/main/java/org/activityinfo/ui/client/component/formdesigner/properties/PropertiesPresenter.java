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
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import org.activityinfo.model.form.FormField;
import org.activityinfo.ui.client.component.formdesigner.WidgetContainer;

/**
 * @author yuriyz on 7/9/14.
 */
public class PropertiesPresenter {

    private final PropertiesPanel view;

    public PropertiesPresenter(PropertiesPanel view) {
        this.view = view;
    }

    public PropertiesPanel getView() {
        return view;
    }

    public void show(final WidgetContainer widgetContainer) {
        final FormField formField = widgetContainer.getFormField();

        view.setVisible(true);
        view.getLabel().setValue(Strings.nullToEmpty(formField.getLabel()));

        view.getLabel().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                formField.setLabel(view.getLabel().getValue());
                widgetContainer.getLabel().setHTML(view.getLabel().getValue());
            }
        });

//        if (formField.getType() == QuantityType.TINSTANCE) {
            //todo
//        }
    }
}
