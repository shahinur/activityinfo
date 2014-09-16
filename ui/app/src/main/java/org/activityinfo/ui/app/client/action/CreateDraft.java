package org.activityinfo.ui.app.client.action;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.ui.flux.action.Action;
import org.activityinfo.ui.flux.store.Store;

/**
 * Creates a "draft" of a new resource.
 *
 */
public class CreateDraft implements Action<DraftHandler> {

    private Resource resource;

    public CreateDraft(Resource resource) {
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    @Override
    public void accept(Store store) {
        if(store instanceof DraftHandler) {
            ((DraftHandler) store).newDraft(this);
        }
    }
}

