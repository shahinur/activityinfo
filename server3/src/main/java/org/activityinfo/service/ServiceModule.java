package org.activityinfo.service;

import com.google.inject.Singleton;
import org.activityinfo.server.util.jaxrs.AbstractRestModule;
import org.activityinfo.service.store.ResourceNotFoundMapper;

public class ServiceModule extends AbstractRestModule {

    @Override
    protected void configureResources() {
        bindResource(ServiceResources.class);
        bindResource(PingService.class);
        bind(ResourceNotFoundMapper.class).in(Singleton.class);
    }
}
