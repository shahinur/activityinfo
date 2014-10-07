package org.activityinfo.ui.app.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Sets;
import com.mysql.jdbc.Driver;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.service.PingService;
import org.activityinfo.store.hrd.HrdResourceStore;
import org.activityinfo.store.hrd.StoreContext;
import org.activityinfo.store.migrate.MigrateDatabaseTask;
import org.activityinfo.store.migrate.MigrateService;
import org.activityinfo.store.tasks.HrdTaskContextProvider;
import org.activityinfo.store.tasks.HrdUserTaskService;

import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class TestApplication extends DefaultResourceConfig {

    private static final Logger LOGGER = Logger.getLogger(TestApplication.class.getName());

    public TestApplication() {
        LocaleProxy.initialize();
    }


    @Override
    public Set<Object> getSingletons() {
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider(ObjectMapperFactory.get());
        MigrateService migrateService = migrateService();
        HrdResourceStore store = new HrdResourceStore(new StoreContext());
        DevUserBlobService blobFieldStorageService = new DevUserBlobService();
        HrdUserTaskService taskService = new HrdUserTaskService(new HrdTaskContextProvider(store, blobFieldStorageService));
        return Sets.newHashSet(jsonProvider, store, migrateService, taskService, blobFieldStorageService,
            new DevIoCProviderFactory());
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
        return Sets.<Class<?>>newHashSet(HostPage.class, PingService.class);
    }
}
