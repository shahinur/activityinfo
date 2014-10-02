package org.activityinfo.service.tasks;

import org.activityinfo.model.annotation.RecordBean;
import org.activityinfo.model.annotation.Reference;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;

@RecordBean(classId = "_exportFormTask")
public class ExportFormTask implements TaskModel {

    private ResourceId formClassId;

    @Reference(range = FormClass.class)
    public ResourceId getFormClassId() {
        return formClassId;
    }

    public void setFormClassId(ResourceId formClassId) {
        this.formClassId = formClassId;
    }
}
