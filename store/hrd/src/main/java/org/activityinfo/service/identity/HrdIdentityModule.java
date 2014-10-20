package org.activityinfo.service.identity;

import com.google.inject.AbstractModule;

public class HrdIdentityModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(IdentityService.class).to(HrdIdentityService.class);
    }
}
