package org.activityinfo.store.hrd;

import com.google.inject.servlet.ServletModule;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.store.migrate.MigrateService;

public class HrdResourceStoreModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(ResourceStore.class).to(HrdResourceStore.class);
        bind(MigrateService.class);
    }
}
