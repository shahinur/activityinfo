package org.activityinfo.server.command;

import com.google.inject.ImplementedBy;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;

@ImplementedBy(ResourceLocatorSyncImpl.class)
public interface ResourceLocatorSync {
    void persist(FormInstance formInstance);

    Resource get(ResourceId resourceId);

}
