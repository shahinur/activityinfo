package org.activityinfo.ui.app.client.request;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;

public class FetchResource implements Request<UserResource> {

    private ResourceId resourceId;

    public FetchResource(ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public Promise<UserResource> send(RemoteStoreService service) {
        return service.get(resourceId);
    }
}
