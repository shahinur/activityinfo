package org.activityinfo.store.hrd;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.CuidGenerator;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.tasks.TaskContext;
import org.activityinfo.store.tasks.HrdUserTaskService;
import org.activityinfo.store.tasks.TaskContextProvider;
import org.activityinfo.store.tasks.TestingTaskContext;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestingEnvironment extends TestWatcher {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setApplyAllHighRepJobPolicy());


    private HrdResourceStore store;
    private HrdUserTaskService taskService;
    private AuthenticatedUser user;
    private CuidGenerator cuidGenerator;

    @Override
    protected void starting(Description description) {
        helper.setUp();
        store = new HrdResourceStore();
        taskService = new HrdUserTaskService(new TaskContextProvider() {
            @Override
            public TaskContext create(AuthenticatedUser user) {
                return new TestingTaskContext(TestingEnvironment.this);
            }
        });
        user = new AuthenticatedUser("XYZ", 1, "test@test.org");
        cuidGenerator = new CuidGenerator(1, System.currentTimeMillis());

        LocaleProxy.initialize();
    }

    @Override
    protected void finished(Description description) {
        helper.tearDown();
    }

    public HrdResourceStore getStore() {
        return store;
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    public HrdUserTaskService getTaskService() { return taskService; }

    public ResourceId generateId() {
        return cuidGenerator.generateResourceId();
    }

    public ResourceId generateWorkspaceId() {
        return cuidGenerator.generateResourceId();
    }

}
