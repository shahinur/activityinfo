package org.activityinfo.migrator.globals;

import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

public class GeographicDatabase extends SingletonMigrator {

    public static final String RESOURCE_ID = "bgeodb";

    @Override
    protected Resource getResource() {
        return Resources.createResource()
        .setId(RESOURCE_ID)
        .setOwnerId(Resources.ROOT_RESOURCE_ID)
        .set("name", "Geographic Reference Database");
    }
}
