package org.activityinfo.store.cloudsql;

import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.sql.SQLException;

public class TestingEnvironment extends TestWatcher {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());


    private MySqlResourceStore store;
    private AuthenticatedUser user;
    private TestingConnectionProvider connectionProvider;

    @Override
    protected void starting(Description description) {
        helper.setUp();

        try {
            connectionProvider = new TestingConnectionProvider();
            connectionProvider.clearTables();

        } catch(Exception e) {
            throw new RuntimeException(e);
        }

        store = new MySqlResourceStore(connectionProvider,
                MemcacheServiceFactory.getMemcacheService());

        user = new AuthenticatedUser("XYZ", 1, "test@test.org");
    }

    @Override
    protected void finished(Description description) {
        helper.tearDown();
    }

    public MySqlResourceStore getStore() {
        return store;
    }

    public void setStore(MySqlResourceStore store) {
        this.store = store;
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    public void assertThatAllConnectionsHaveBeenClosed() throws SQLException {
        connectionProvider.assertThatAllConnectionsHaveBeenClosed();
    }
}
