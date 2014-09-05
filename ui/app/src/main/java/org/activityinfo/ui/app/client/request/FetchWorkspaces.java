package org.activityinfo.ui.app.client.request;

import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;

import java.util.List;

/**
 *
 */
public class FetchWorkspaces implements Request<List<ResourceNode>> {

    @Override
    public Promise<List<ResourceNode>> send(RemoteStoreService service) {
        return service.getWorkspaces();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof FetchWorkspaces;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}
