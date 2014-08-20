package org.activityinfo.service;

import org.activityinfo.server.util.jaxrs.AbstractRestModule;

public class ServiceModule extends AbstractRestModule {

    @Override
    protected void configureResources() {
        bindResource(ServiceResources.class);
    }
}
