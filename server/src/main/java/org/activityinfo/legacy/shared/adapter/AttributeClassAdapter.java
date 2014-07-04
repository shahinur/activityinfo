package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import org.activityinfo.core.shared.Cuid;
import java.lang.String;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.model.AttributeGroupDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;

import javax.annotation.Nullable;

/**
 * Converts an {@code AttributeGroupDTO} to a {@code FormClass}
 */
public class AttributeClassAdapter implements Function<SchemaDTO, FormClass> {

    private int attributeGroupId;

    public AttributeClassAdapter(int attributeGroupId) {
        this.attributeGroupId = attributeGroupId;
    }

    @Nullable @Override
    public FormClass apply(@Nullable SchemaDTO schema) {
        AttributeGroupDTO group = schema.getAttributeGroupById(attributeGroupId);
        Cuid classId = CuidAdapter.attributeGroupFormClass(group);
        FormClass formClass = new FormClass(classId);
        formClass.setLabel(group.getName());

        // attributes have only one field- the label
        FormField labelField = new FormField(CuidAdapter.field(classId, CuidAdapter.NAME_FIELD));
        labelField.setLabel(I18N.CONSTANTS.labelFieldLabel());
        labelField.setType(FormFieldType.FREE_TEXT);
        labelField.setRequired(true);
        formClass.addElement(labelField);

        return formClass;
    }
}
