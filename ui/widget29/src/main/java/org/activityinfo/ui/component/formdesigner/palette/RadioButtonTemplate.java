package org.activityinfo.ui.component.formdesigner.palette;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.resource.Resources;
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
        values.add(new EnumValue(Resources.generateId(), "Choice 1"));
        values.add(new EnumValue(Resources.generateId(), "Choice 2"));
        FormField field = new FormField(Resources.generateId());
        field.setLabel("Which choice would you choose?");
        field.setType(new EnumType(Cardinality.SINGLE, values));

        return field;
    }
}
