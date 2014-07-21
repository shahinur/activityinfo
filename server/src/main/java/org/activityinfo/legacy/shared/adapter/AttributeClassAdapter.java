package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.model.AttributeGroupDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;
import org.activityinfo.model.type.TextType;

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
        ResourceId classId = CuidAdapter.attributeGroupFormClass(group.getId());
        FormClass formClass = new FormClass(classId);
        formClass.setLabel(group.getName());

        // attributes have only one field- the label
        FormField labelField = new FormField(CuidAdapter.field(classId, CuidAdapter.NAME_FIELD));
        labelField.setLabel(I18N.CONSTANTS.labelFieldLabel());
        labelField.setType(TextType.INSTANCE);
        labelField.setRequired(true);
        formClass.addElement(labelField);

        return formClass;
    }
}
