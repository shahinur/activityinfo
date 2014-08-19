package org.activityinfo.service.blob;

import org.activityinfo.server.endpoint.rest.RestApiModule;

public class GcsBlobFieldStorageServiceModule extends RestApiModule {

    @Override
    protected void configureResources() {
        bindResource(GcsBlobFieldStorageService.class);
    }
}
