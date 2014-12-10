package org.activityinfo.store.hrd;

import com.google.inject.servlet.ServletModule;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.tasks.UserTaskService;
import org.activityinfo.service.tasks.appengine.AppEngineContextProvider;
import org.activityinfo.service.tasks.appengine.AppEngineUserTaskService;
import org.activityinfo.service.tasks.appengine.TaskContextProvider;

public class HrdResourceStoreModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(ResourceStore.class).to(HrdResourceStore.class);
        bind(UserTaskService.class).to(AppEngineUserTaskService.class);
        bind(TaskContextProvider.class).to(AppEngineContextProvider.class);
    }
}
