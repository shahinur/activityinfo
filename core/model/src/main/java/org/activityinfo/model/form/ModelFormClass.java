package org.activityinfo.model.form;

import org.activityinfo.model.resource.Resource;

public interface ModelFormClass<T> {

    T createAccessor(Resource resource);




}
