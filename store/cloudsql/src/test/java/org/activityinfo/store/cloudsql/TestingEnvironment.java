package org.activityinfo.store.cloudsql;

import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.activityinfo.model.resource.ResourceId;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.IOException;
import java.sql.SQLException;

public class TestingEnvironment extends TestWatcher {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());


    private MySqlResourceStore store;
    private ResourceId userId;
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

        userId = ResourceId.generateId();

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

    public ResourceId getUserId() {
        return userId;
    }

    public void assertThatAllConnectionsHaveBeenClosed() throws SQLException {
        connectionProvider.assertThatAllConnectionsHaveBeenClosed();
    }
}
