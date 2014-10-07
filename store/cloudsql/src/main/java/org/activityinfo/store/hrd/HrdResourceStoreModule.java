package org.activityinfo.store.hrd;

import com.google.inject.servlet.ServletModule;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.tasks.UserTaskService;
import org.activityinfo.store.migrate.MigrateService;
import org.activityinfo.store.tasks.HrdTaskContextProvider;
import org.activityinfo.store.tasks.HrdUserTaskService;
import org.activityinfo.store.tasks.TaskContextProvider;

public class HrdResourceStoreModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(ResourceStore.class).to(HrdResourceStore.class);
        bind(MigrateService.class);
        bind(UserTaskService.class).to(HrdUserTaskService.class);
        bind(TaskContextProvider.class).to(HrdTaskContextProvider.class);
    }
}
