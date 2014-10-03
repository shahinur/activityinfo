package org.activityinfo.ui.app.server;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.servlet.ServletContainer;

public class DevServletContainer extends ServletContainer {

    @Override
    protected void initiate(ResourceConfig rc, WebApplication wa) {
        wa.initiate(rc, new DevIoCProviderFactory());
    }
}
