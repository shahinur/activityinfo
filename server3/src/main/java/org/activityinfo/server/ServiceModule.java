package org.activityinfo.server;

import com.google.inject.Singleton;
import org.activityinfo.server.util.jaxrs.AbstractRestModule;
import org.activityinfo.service.PingService;
import org.activityinfo.service.blob.UserBlobRestfulService;
import org.activityinfo.service.store.ResourceNotFoundMapper;
import org.activityinfo.service.store.ResourceStoreRestfulService;

public class ServiceModule extends AbstractRestModule {

    @Override
    protected void configureResources() {
        bindResource(UserBlobRestfulService.class);
        bindResource(ResourceStoreRestfulService.class);
        bindResource(PingService.class);
        bind(ResourceNotFoundMapper.class).in(Singleton.class);
    }
}
