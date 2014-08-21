package org.activityinfo.service;

import org.activityinfo.server.util.jaxrs.AbstractRestModule;
import org.activityinfo.service.store.ResourceNotFoundMapper;

public class ServiceModule extends AbstractRestModule {

    @Override
    protected void configureResources() {
        bindResource(ServiceResources.class);
        bind(ResourceNotFoundMapper.class);
    }
}
