package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.store.StoreAccessor;
import org.activityinfo.store.hrd.entity.LatestContent;

public class HrdStoreAccessor implements StoreAccessor {

    private DatastoreService datastore;

    public HrdStoreAccessor(DatastoreService datastore) {
        this.datastore = datastore;
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) throws Exception {
        return new HrdCursor(LatestContent.queryInstances(datastore, formClassId));
    }

    @Override
    public Resource get(ResourceId formClassId) throws Exception {
        return LatestContent.get(datastore, formClassId);
    }

    @Override
    public void close() {

    }
}
