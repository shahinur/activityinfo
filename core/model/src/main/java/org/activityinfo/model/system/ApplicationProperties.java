package org.activityinfo.model.system;

import org.activityinfo.model.analysis.PivotTableModelClass;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;

/**
 * Defines Application-level properties.
 * <strong>Should not be used as a form field id, only as a super property</strong>
 */
public class ApplicationProperties {

    /**
     * Application-defined property that provides a human-readable name for
     * a given form instance.
     */
    public static final ResourceId LABEL_PROPERTY = ResourceId.valueOf("label");

    public static final ResourceId PARENT_PROPERTY = ResourceId.valueOf("_parent");

    /**
     * Application-defined property that provides an extended human-readable description
     * for a given form instance.
     */
    public static final ResourceId DESCRIPTION_PROPERTY = ResourceId.valueOf("_description");

    /**
     * Application-defined property that provides the class ids of an instance.
     */
    public static final ResourceId CLASS_PROPERTY = ResourceId.valueOf("_classOf");


    public static final ResourceId HIERARCHIAL = ResourceId.valueOf("_multiLevel");

    public static final ResourceId WITHIN = ResourceId.valueOf("_geo:within");


    public static String getLabelPropertyName(ResourceId classId) {
        if (FormClass.CLASS_ID.equals(classId)) {
            return FormClass.LABEL_FIELD_ID;

        } else if (FolderClass.CLASS_ID.equals(classId)) {
            return FolderClass.LABEL_FIELD_NAME;

        } else if (PivotTableModelClass.CLASS_ID.equals(classId)) {
            return PivotTableModelClass.LABEL_FIELD_NAME;
        }
        return null;
    }

    /**
     *
     * @return {@code true} if resources with class {@code formClassId} should be
     * displayed within a folder
     */
    public static boolean isFolderItem(ResourceId formClassId) {
        return formClassId.equals(FormClass.CLASS_ID) ||
            formClassId.equals(FolderClass.CLASS_ID) ||
            formClassId.equals(PivotTableModelClass.CLASS_ID);
    }
}
