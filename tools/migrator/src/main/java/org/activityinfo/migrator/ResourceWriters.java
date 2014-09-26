package org.activityinfo.migrator;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.system.FolderClass;

public class ResourceWriters {
    public static String getLabel(Resource resource) {
        String classId = resource.getValue().isString("classId");
        if(FormClass.CLASS_ID.asString().equals(classId)) {
            return resource.getValue().getString(FormClass.LABEL_FIELD_ID);
        } else if(FolderClass.CLASS_ID.asString().equals(classId)) {
            return resource.getValue().isString(FolderClass.LABEL_FIELD_ID.asString());
        }
        return null;
    }
}
