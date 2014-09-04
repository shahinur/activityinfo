package org.activityinfo.ui.app.client.request;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.service.store.UpdateResult;

public class SaveRequest implements Request<UpdateResult> {

    private Resource resource;

    public SaveRequest(Resource resource) {
        this.resource = resource;
    }

    public ResourceId getResourceId() {
        return resource.getId();
    }

    public Resource getUpdatedResource() {
        return resource;
    }

    @Override
    public Promise<UpdateResult> send(RemoteStoreService service) {
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
