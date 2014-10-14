package org.activityinfo.service.tree;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;

public interface FormClassProvider {

    FormClass getFormClass(ResourceId resourceId);
}
