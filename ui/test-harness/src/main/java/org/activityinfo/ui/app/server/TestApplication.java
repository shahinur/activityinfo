package org.activityinfo.ui.app.server;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.common.collect.Sets;
import com.mysql.jdbc.Driver;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.service.PingService;
import org.activityinfo.service.blob.UserBlobRestfulService;
import org.activityinfo.service.store.ResourceStoreRestfulService;
import org.activityinfo.service.tasks.UserTaskRestfulService;
import org.activityinfo.service.tasks.appengine.*;
import org.activityinfo.store.hrd.HrdResourceStore;
import org.activityinfo.store.hrd.StoreContext;
import org.activityinfo.store.migrate.MigrateDatabaseTask;
import org.activityinfo.store.migrate.MigrateService;

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

        HrdResourceStore store = new HrdResourceStore(new StoreContext());
        MigrateService migrateService = migrateService(store);

        DevUserBlobService blobService = new DevUserBlobService();

        AppEngineUserTaskService taskService = new AppEngineUserTaskService(new TaskStore(), new TaskExecutors());
        TaskRunner taskRunner = new TaskRunner(new TaskStore(), new TaskExecutors(), new AppEngineContextProvider(store, blobService));


        return Sets.newHashSet(jsonProvider,
            store,
            migrateService,
            taskService,
            blobService,
            taskRunner,
            new UserTaskRestfulService(taskService),
            new UserBlobRestfulService(blobService),
            new ResourceStoreRestfulService(store),
            new DevIoCProviderFactory());
    }

    private MigrateService migrateService(HrdResourceStore store) {
        Properties properties = new Properties();
        properties.setProperty(MigrateDatabaseTask.MIGRATION_DRIVER_CLASS, Driver.class.getName());
        properties.setProperty(MigrateDatabaseTask.MIGRATION_SOURCE_URL, "jdbc:mysql://127.0.0.1:3306/activityinfo");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_USER, "root");
        properties.setProperty(MigrateDatabaseTask.MIGRATION_PASS, "root");
        return new MigrateService(store, new DeploymentConfiguration(properties));
    }

    @Override
    public Set<Class<?>> getClasses() {
        return Sets.<Class<?>>newHashSet(HostPage.class, PingService.class);
    }
}
