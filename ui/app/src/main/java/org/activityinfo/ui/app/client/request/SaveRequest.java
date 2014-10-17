package org.activityinfo.ui.app.client.request;

import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.model.resource.IsResource;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.UpdateResult;

public class SaveRequest implements Request<UpdateResult> {

    private Resource resource;

    public SaveRequest(Resource resource) {
        this.resource = resource;
    }

    public SaveRequest(IsResource resource) {
        this.resource = resource.asResource();
    }

    public ResourceId getResourceId() {
        return resource.getId();
    }

    public Resource getUpdatedResource() {
        return resource.copy();
    }

    @Override
    public Promise<UpdateResult> send(ActivityInfoAsyncClient service) {
        if(resource.getVersion() == 0) {
            return service.create(resource);
        } else {
            return service.put(resource);
        }
    }

    public boolean isNewResource() {
        return resource.getVersion() == 0;
    }
}
