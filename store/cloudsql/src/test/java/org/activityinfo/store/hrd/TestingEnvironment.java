package org.activityinfo.store.hrd;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.CuidGenerator;
import org.activityinfo.model.resource.ResourceId;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class TestingEnvironment extends TestWatcher {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setApplyAllHighRepJobPolicy());


    private HrdResourceStore store;
    private AuthenticatedUser user;
    private CuidGenerator cuidGenerator;

    @Override
    protected void starting(Description description) {
        helper.setUp();
        store = new HrdResourceStore();

        user = new AuthenticatedUser("XYZ", 1, "test@test.org");
        cuidGenerator = new CuidGenerator(store.generateClientId(user), System.currentTimeMillis());
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


    public ResourceId generateId() {
        return cuidGenerator.generateResourceId();
    }

    public ResourceId generateWorkspaceId() {
        return cuidGenerator.generateResourceId();
    }

}
