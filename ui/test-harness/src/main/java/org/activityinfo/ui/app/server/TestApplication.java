package org.activityinfo.ui.app.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Sets;
import com.mysql.jdbc.Driver;
import com.sun.jersey.api.core.DefaultResourceConfig;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.store.hrd.HrdResourceStore;
import org.activityinfo.store.migrate.MigrateDatabaseTask;
import org.activityinfo.store.migrate.MigrateService;

import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class TestApplication extends DefaultResourceConfig {

    private static final Logger LOGGER = Logger.getLogger(TestApplication.class.getName());

    public TestApplication() {
    }

    @Override
    public Set<Object> getSingletons() {
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider(ObjectMapperFactory.get());
        MigrateService migrateService = migrateService();
       // AuthenticatedUser user = new AuthenticatedUser("XYZ", 1, "test@test.org");
        return Sets.newHashSet(jsonProvider, migrateService);
    }

    private MigrateService migrateService() {
        Properties properties = new Properties();
        properties.setProperty(MigrateDatabaseTask.MIGRATION_DRIVER_CLASS, Driver.class.getName());
        properties.setProperty(MigrateDatabaseTask.MIGRATION_SOURCE_URL, "jdbc:mysql://127.0.0.1:3306/activityinfo");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_USER, "root");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_PASS, "root");
        return new MigrateService(new DeploymentConfiguration(properties));
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.newHashSet(HrdResourceStore.class, HostPage.class);
    }
}
