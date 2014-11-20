package org.activityinfo.ui.client.component.formdesigner.palette;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.CheckBox;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.legacy.KeyGenerator;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.Cardinality;
import org.activityinfo.model.type.enumerated.EnumType;
import org.activityinfo.model.type.enumerated.EnumValue;

import java.util.List;

public class CheckboxTemplate implements FieldTemplate {
    @Override
    public String getLabel() {
        return I18N.CONSTANTS.checkboxes();
    }

    @Override
    public FormField createField() {
        List<EnumValue> values = Lists.newArrayList();
        values.add(new EnumValue(EnumValue.generateId(), "Option 1"));
        values.add(new EnumValue(EnumValue.generateId(), "Option 2"));
        FormField field = new FormField(ResourceId.generateFieldId(EnumType.TYPE_CLASS));
        field.setLabel("Which options apply?");
        field.setType(new EnumType(Cardinality.MULTIPLE, values));

        return field;
    }
}
