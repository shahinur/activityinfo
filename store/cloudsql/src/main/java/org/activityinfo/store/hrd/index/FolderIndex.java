package org.activityinfo.store.hrd.index;

import com.google.appengine.api.datastore.Entity;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.system.FolderClass;

public class FolderIndex {

    public static final String KIND = "Folder";

    public static boolean isFolderItem(Resource resource) {
        return getLabel(resource) != null;
    }

    public static Key

    private static String getLabel(Resource resource) {
        String classId = resource.isString("classId");
        if(FormClass.CLASS_ID.asString().equals(classId)) {
            return resource.isString(FormClass.LABEL_FIELD_ID);

        } else if(FolderClass.CLASS_ID.asString().equals(classId)) {
            return resource.isString(FolderClass.LABEL_FIELD_ID.asString());
        }
        return null;
    }

    public static Entity createEntity(Resource resource) {
        Entity entity = new Entity()
    }
}
