package org.activityinfo.ui.app.client.draft;

import org.activityinfo.model.resource.Resource;

public class Draft {


    private final Resource resource;

    /**
     * Whether any version of this resource has
     * been created on the server.
     */
    private boolean created;

    private Draft(Resource resource) {
        this.resource = resource;
        this.created = false;
    }

    public Resource getResource() {
        // defensive copy
        return resource.copy();
    }


    public static Draft create(Resource resource) {
        Draft draft = new Draft(resource);
        draft.created = false;
        return draft;
    }
}
