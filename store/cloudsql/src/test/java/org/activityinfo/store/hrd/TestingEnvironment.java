package org.activityinfo.store.hrd;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.service.store.StoreReader;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tasks.appengine.AppEngineUserTaskService;
import org.activityinfo.service.tasks.appengine.TaskExecutors;
import org.activityinfo.service.tasks.appengine.TaskStore;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestingEnvironment extends TestWatcher {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setApplyAllHighRepJobPolicy());


    private HrdResourceStore store;
    private AppEngineUserTaskService taskService;
    private AuthenticatedUser user;
    private CuidGenerator cuidGenerator;
    private StoreContext storeContext;

    @Override
    protected void starting(Description description) {
        helper.setUp();
        storeContext = new StoreContext();
        store = new HrdResourceStore(storeContext);
        taskService = new AppEngineUserTaskService(new TaskStore(), new TaskExecutors());
        user = new AuthenticatedUser("XYZ", 1, "test@test.org");
        cuidGenerator = new CuidGenerator(1, System.currentTimeMillis());

        LocaleProxy.initialize();
    }

    public StoreContext getContext() {
        return storeContext;
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

    public AppEngineUserTaskService getTaskService() { return taskService; }

    public ResourceId generateId() {
        return cuidGenerator.generateResourceId();
    }

    public ResourceId generateWorkspaceId() {
        return cuidGenerator.generateResourceId();
    }

    public UpdateResult put(Resource updatedInstance) {
        return store.put(getUser(), updatedInstance);
    }

    public UserResource getResource(ResourceId id) {
        return store.get(getUser(), id);
    }

    public FolderProjection queryTree(ResourceId parentId) {
        return store.queryTree(getUser(), new FolderRequest(parentId));
    }

    public TableData queryTable(TableModel tableModel) {

        try(StoreReader reader = store.openReader(user)) {
            return reader.getTable(tableModel);
        }
    }
}
