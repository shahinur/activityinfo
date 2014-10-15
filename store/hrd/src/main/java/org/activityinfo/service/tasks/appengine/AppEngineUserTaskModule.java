package org.activityinfo.service.tasks.appengine;

import com.google.inject.AbstractModule;
import org.activityinfo.service.tasks.UserTaskService;

public class AppEngineUserTaskModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserTaskService.class).to(AppEngineUserTaskService.class);
    }
}
