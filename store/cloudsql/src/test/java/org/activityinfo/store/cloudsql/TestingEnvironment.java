package org.activityinfo.store.cloudsql;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.store.hrd.HrdResourceStore;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.sql.SQLException;

public class TestingEnvironment extends TestWatcher {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setApplyAllHighRepJobPolicy());


    private HrdResourceStore store;
    private AuthenticatedUser user;
    private TestingConnectionProvider connectionProvider;

    @Override
    protected void starting(Description description) {
        helper.setUp();
        store = new HrdResourceStore(DatastoreServiceFactory.getDatastoreService());

        user = new AuthenticatedUser("XYZ", 1, "test@test.org");
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

    public void assertThatAllConnectionsHaveBeenClosed() throws SQLException {
       // connectionProvider.assertThatAllConnectionsHaveBeenClosed();
    }
}
