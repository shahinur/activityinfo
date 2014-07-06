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

import com.google.common.collect.Sets;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.form.FormFieldCardinality;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.form.FormInstanceLabeler;
import org.activityinfo.ui.client.component.form.model.SimpleListViewModel;
import org.activityinfo.ui.client.widget.RadioButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author yuriyz on 2/11/14.
 */
public class CheckBoxFieldWidget implements ReferenceFieldWidget {

    private final FlowPanel panel;
    private final List<CheckBox> controls;
    private SimpleEventBus eventBus;

    public CheckBoxFieldWidget(SimpleListViewModel range, final ValueUpdater valueUpdater) {
        panel = new FlowPanel();
        controls = new ArrayList<>();

        ValueChangeHandler<Boolean> changeHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                valueUpdater.update(updatedValue());
            }
        };

        for (final FormInstance instance : range.getInstances()) {
            CheckBox checkBox = createControl(instance, range.getCardinality());
            checkBox.addValueChangeHandler(changeHandler);
            panel.add(checkBox);
            controls.add(checkBox);
        }
    }

    private CheckBox createControl(FormInstance instance, FormFieldCardinality cardinality) {
        CheckBox checkBox;
        String label = FormInstanceLabeler.getLabel(instance);
        if(cardinality == FormFieldCardinality.SINGLE) {
            checkBox = new RadioButton(instance.getId().asString(), label);
        } else {
            checkBox = new CheckBox(label);
            checkBox.setFormValue(instance.getId().asString());
        }
        return checkBox;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        for (CheckBox control : controls) {
            control.setEnabled(!readOnly);
        }
    }

    private Set<ResourceId> updatedValue() {
        final Set<ResourceId> value = Sets.newHashSet();
        for (CheckBox control : controls) {
            if(control.getValue()) {
                value.add(ResourceId.create(control.getFormValue()));
            }
        }
        return value;
    }

    @Override
    public void setValue(Set<ResourceId> value) {
        for (CheckBox entry : controls) {
            ResourceId resourceId = ResourceId.create(entry.getFormValue());
            entry.setValue(value.contains(resourceId));
        }
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
