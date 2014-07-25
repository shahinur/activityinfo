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

import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.TextType;
import org.activityinfo.model.type.TypeRegistry;
import org.activityinfo.ui.client.component.formdesigner.container.FieldWidgetContainer;

import java.util.List;

/**
 * @author yuriyz on 7/24/14.
 */
public class SkipRowPresenter {

    final FieldWidgetContainer fieldWidgetContainer;
    private final SkipRow view = new SkipRow();

    public SkipRowPresenter(final FieldWidgetContainer fieldWidgetContainer) {
        this.fieldWidgetContainer = fieldWidgetContainer;
        initFormField(view.getFormfield());
    }

    private void initFormField(ListBox formfieldBox) {
        int defaultValueIndex = 0;

        List<FieldTypeClass> registeredTypes = Lists.newArrayList(TypeRegistry.get().getTypeClasses());
        for (int i = 0; i < registeredTypes.size(); i++) {
            formfieldBox.addItem(registeredTypes.get(i).getLabel(), registeredTypes.get(i).getId());
            if (registeredTypes.get(i).getId() == TextType.INSTANCE.getId()) {
                defaultValueIndex = i;
            }
        }
        formfieldBox.setSelectedIndex(defaultValueIndex);
        formfieldBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {

            }
        });
    }

    public SkipRow getView() {
        return view;
    }
}
