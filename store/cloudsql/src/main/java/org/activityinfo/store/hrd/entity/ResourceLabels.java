package org.activityinfo.store.hrd.entity;

import com.google.common.base.Optional;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.system.FolderClass;

public class ResourceLabels {

    public static Optional<String> getLabel(Resource resource) {
        String classId = resource.isString("classId");

        if(FormClass.CLASS_ID.asString().equals(classId)) {
            return Optional.fromNullable(resource.isString(FormClass.LABEL_FIELD_ID));

        } else if(FolderClass.CLASS_ID.asString().equals(classId)) {
            return Optional.fromNullable(resource.isString(FolderClass.LABEL_FIELD_ID.asString()));

        } else {
            return Optional.absent();
        }
    }
}
