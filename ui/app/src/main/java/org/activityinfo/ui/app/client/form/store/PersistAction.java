package org.activityinfo.ui.app.client.form.store;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.flux.action.Action;
import org.activityinfo.ui.flux.store.Store;

public class PersistAction implements Action<PersistAction> {
    private ResourceId resourceId;

    public PersistAction(ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public Promise<Void> accept(Store store) {
        if(store instanceof PersistHandler) {
            ((PersistHandler) store).persistInstance(resourceId);
        }
        return Promise.done();
    }
}
