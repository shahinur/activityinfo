package org.activityinfo.ui.client.component.formdesigner.palette;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;

import java.util.List;

public class RadioButtonTemplate implements FieldTemplate {

    @Override
    public String getLabel() {
        return "Dropdown/Radio";
    }

    @Override
    public FormField createField() {
        List<EnumValue> values = Lists.newArrayList();
        values.add(new EnumValue(ResourceId.generateId(), "Choice 1"));
        values.add(new EnumValue(ResourceId.generateId(), "Choice 2"));
        FormField field = new FormField(ResourceId.generateId());
        field.setLabel("Which choice would you choose?");
        field.setType(new EnumType(Cardinality.SINGLE, values));

        return field;
    }
}
