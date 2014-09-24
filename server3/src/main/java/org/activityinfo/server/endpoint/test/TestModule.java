package org.activityinfo.server.endpoint.test;

import org.activityinfo.server.util.jaxrs.AbstractRestModule;

public class TestModule extends AbstractRestModule {
    @Override
    protected void configureResources() {
        bindResource(TestEndpoint.class);
    }
}
