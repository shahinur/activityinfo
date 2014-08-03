package org.activityinfo.service.core;

import com.google.inject.AbstractModule;
import org.activityinfo.model.table.TableService;
import org.activityinfo.server.util.jaxrs.AbstractRestModule;
import org.activityinfo.service.core.store.MySqlResourceStore;
import org.activityinfo.service.core.store.ResourceStore;
import org.activityinfo.service.core.tables.TableServiceImpl;

public class ServiceModule extends AbstractRestModule {

    @Override
    protected void configureResources() {
        bindResource(ServiceResources.class);
        bind(ResourceStore.class).to(MySqlResourceStore.class);
        bind(TableService.class).to(TableServiceImpl.class);
    }
}
