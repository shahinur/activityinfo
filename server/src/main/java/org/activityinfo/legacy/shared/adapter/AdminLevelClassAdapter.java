package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import org.activityinfo.core.shared.Cuid;
import java.lang.String;
import org.activityinfo.core.shared.application.ApplicationProperties;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.model.AdminLevelDTO;
import org.activityinfo.legacy.shared.model.SchemaDTO;

import static org.activityinfo.legacy.shared.adapter.CuidAdapter.adminLevelFormClass;

/**
 * Extracts a given AdminLevel from a provided SchemaDTO and converts it to a FormClass
 */
public class AdminLevelClassAdapter implements Function<SchemaDTO, FormClass> {

    private final int adminLevelId;

    public AdminLevelClassAdapter(int adminLevelId) {
        this.adminLevelId = adminLevelId;
    }


    public static Cuid getNameFieldId(Cuid classId) {
        return CuidAdapter.field(classId, CuidAdapter.NAME_FIELD);
    }

    @Override
    public FormClass apply(SchemaDTO schema) {
        AdminLevelDTO adminLevel = schema.getAdminLevelById(adminLevelId);

        Cuid classId = adminLevelFormClass(adminLevelId);
        FormClass formClass = new FormClass(classId);
        formClass.setLabel(adminLevel.getName());

        if (adminLevel.isRoot()) {
            // TODO add country field
        } else {
            AdminLevelDTO parentLevel = schema.getAdminLevelById(adminLevel.getParentLevelId());
            FormField parentField = new FormField(CuidAdapter.field(classId, CuidAdapter.ADMIN_PARENT_FIELD));
            parentField.setLabel(parentLevel.getName());
            parentField.setSuperProperty(ApplicationProperties.PARENT_PROPERTY);
            parentField.setRange(adminLevelFormClass(adminLevel.getParentLevelId()));
            parentField.setType(FormFieldType.REFERENCE);
            parentField.setRequired(true);
            formClass.addElement(parentField);
        }

        FormField nameField = new FormField(getNameFieldId(classId));
        nameField.setLabel(I18N.CONSTANTS.name());
        nameField.setType(FormFieldType.FREE_TEXT);
        nameField.setSuperProperty(ApplicationProperties.LABEL_PROPERTY);
        nameField.setRequired(true);
        formClass.addElement(nameField);


        //    Not currently exposed by the legacy api
        //        FormField codeField = new FormField(CuidAdapter.field(classId, CuidAdapter.CODE_FIELD));
        //        codeField.setLabel(new LocalizedString(I18N.CONSTANTS.codeFieldLabel()));
        //        codeField.setType(FormFieldType.FREE_TEXT);
        //        formClass.addElement(codeField);

        return formClass;
    }
}
