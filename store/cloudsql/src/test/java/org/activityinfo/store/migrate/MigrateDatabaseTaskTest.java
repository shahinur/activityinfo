package org.activityinfo.store.migrate;

import org.activityinfo.store.cloudsql.TestingEnvironment;
import org.junit.Rule;
import org.junit.Test;

import javax.inject.Provider;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MigrateDatabaseTaskTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();

    @Test
    public void test() throws Exception {

        Provider<Connection> connectionProvider = new Provider<Connection>() {

            @Override
            public Connection get() {
                try {
                    return DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/activityinfo", "root", "root");
                } catch (SQLException e) {
                    throw new AssertionError(e);
                }
            }
        };

        MigrateDatabaseTask migrator = new MigrateDatabaseTask(environment.getUser(), environment.getStore(),
            connectionProvider);
        migrator.migrate(1350);

    }

}