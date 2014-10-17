package org.activityinfo.ui.app.client.request;

import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.promise.Promise;

public class FetchResource implements Request<UserResource> {

    private ResourceId resourceId;

    public FetchResource(ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public Promise<UserResource> send(ActivityInfoAsyncClient service) {
        return service.get(resourceId);
    }
}
