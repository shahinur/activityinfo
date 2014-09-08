package org.activityinfo.store.migrate;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.store.hrd.TestingEnvironment;
import org.junit.Rule;
import org.junit.Test;

import java.util.Properties;

public class MigrateDatabaseTaskTest {

    @Rule
    public TestingEnvironment environment = new TestingEnvironment();

    @Test
    //@Ignore("requires local setup")
    public void test() throws Exception {

        Properties properties = new Properties();
        properties.setProperty(MigrateDatabaseTask.MIGRATION_SOURCE_URL, "jdbc:mysql://127.0.0.1:3306/activityinfo");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_USER, "root");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_PASS, "root");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_DRIVER_CLASS, "com.mysql.jdbc.Driver");

        DeploymentConfiguration config = new DeploymentConfiguration(properties);

        MigrateDatabaseTask migrator = new MigrateDatabaseTask(config, new AuthenticatedUser("", 1, ""));
        migrator.migrate(1350);
    }
}