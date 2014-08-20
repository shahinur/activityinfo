package org.activityinfo.ui.component.form.field;
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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.InstanceLabelTable;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.FieldType;
import org.activityinfo.model.type.ReferenceType;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.promise.Promise;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author yuriyz on 2/11/14.
 */
public class CheckBoxFieldWidget implements ReferenceFieldWidget {

    private final FlowPanel panel;
    private final List<CheckBox> controls;
    private SimpleEventBus eventBus;

    public CheckBoxFieldWidget(ReferenceType type, InstanceLabelTable table, final ValueUpdater valueUpdater) {
        panel = new FlowPanel();
        controls = new ArrayList<>();

        ValueChangeHandler<Boolean> changeHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                valueUpdater.update(updatedValue());
            }
        };

        String groupId = Long.toString(new Date().getTime());
        for (int i=0;i!=table.getNumRows(); ++i) {
            CheckBox checkBox = createControl(groupId, table.getId(i), table.getLabel(i), type.getCardinality());
            checkBox.addValueChangeHandler(changeHandler);
            panel.add(checkBox);
            controls.add(checkBox);
        }
    }

    private CheckBox createControl(String groupId, ResourceId id, String label, Cardinality cardinality) {
        CheckBox checkBox;
        if(cardinality == Cardinality.SINGLE) {
            checkBox = new RadioButton(groupId, label);
        } else {
            checkBox = new CheckBox(label);
        }
        checkBox.setFormValue(id.asString());
        return checkBox;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        for (CheckBox control : controls) {
            control.setEnabled(!readOnly);
        }
    }

    private ReferenceValue updatedValue() {
        final Set<ResourceId> value = Sets.newHashSet();
        for (CheckBox control : controls) {
            if(control.getValue()) {
                value.add(ResourceId.valueOf(control.getFormValue()));
            }
        }
        return new ReferenceValue(value);
    }

    @Override
    public Promise<Void> setValue(ReferenceValue value) {
        Set<ResourceId> ids = value.getResourceIds();
        for (CheckBox entry : controls) {
            ResourceId resourceId = ResourceId.valueOf(entry.getFormValue());
            entry.setValue(ids.contains(resourceId));
        }
        return Promise.done();
    }

    @Override
    public void clearValue() {
       setValue(ReferenceValue.EMPTY);
    }

    @Override
    public void setType(FieldType type) {

    }

    @Override
    public Widget asWidget() {
        return panel;
    }

    @Override
    public List<FormInstance> getRange() {
        throw new UnsupportedOperationException("danger will robinson!! mixing UI and logic!!!");
    }
}
