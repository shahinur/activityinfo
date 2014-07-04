package org.activityinfo.legacy.shared.adapter;

import com.google.common.base.Function;
import org.activityinfo.core.shared.Cuid;
import java.lang.String;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.model.ActivityDTO;

import javax.annotation.Nullable;

/**
 * In the old legacy, many "FormClasses" were builtins, defined as part
 * of ActivityInfo's own datamodel. In Api2, these all move to the users
 * control as so become just another FormClass.
 */
public class BuiltinFormClasses {


    /**
     * Partner was a builtin object type in api1. However, we need a different
     * FormClass for each legacy UserDatabase.
     */
    public static FormClass projectFormClass(int databaseId) {

        Cuid classId = CuidAdapter.projectFormClass(databaseId);
        FormClass formClass = new FormClass(classId);
        formClass.setLabel(I18N.CONSTANTS.project());

        // add the project's name
        FormField nameField = new FormField(CuidAdapter.field(classId, CuidAdapter.NAME_FIELD));
        nameField.setLabel(I18N.CONSTANTS.name());
        nameField.setType(FormFieldType.FREE_TEXT);
        nameField.setRequired(true);
        formClass.addElement(nameField);

        return formClass;
    }


    static class ActivityAdapter implements Function<ActivityDTO, FormClass> {

        private int activityId;

        public ActivityAdapter(int activityId) {
            this.activityId = activityId;
        }

        @Nullable @Override
        public FormClass apply(@Nullable ActivityDTO activity) {
            ActivityUserFormBuilder builder = new ActivityUserFormBuilder(activity);
            return builder.build();
        }
    }
}
