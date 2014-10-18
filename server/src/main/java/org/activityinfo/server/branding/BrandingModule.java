package org.activityinfo.server.branding;

import org.activityinfo.server.database.hibernate.entity.Domain;
import org.activityinfo.server.util.jaxrs.AbstractRestModule;

public class BrandingModule extends AbstractRestModule {

    @Override
    protected void configureResources() {
        bind(Domain.class).toProvider(DomainProvider.class);
        bindResource(BrandingConfigResource.class);
    }
}
