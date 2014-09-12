package org.activityinfo.store.migrate;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import org.activityinfo.migrator.MySqlMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.DatabaseFilter;
import org.activityinfo.migrator.filter.FreshIdStrategy;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.migrator.filter.MigrationFilter;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.CuidGenerator;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.store.hrd.ClientIdProvider;
import org.activityinfo.store.hrd.entity.Workspace;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;

import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MigrateDatabaseTask {

    private static final Logger LOGGER = Logger.getLogger(MigrateDatabaseTask.class.getName());

    public static final String MIGRATION_SOURCE_URL = "migration.jdbc.url";
    public static final String MIGRATION_DRIVER_CLASS = "migration.jdbc.driver.class";
    public static final String MIGRATION_USER = "migration.jdbc.username";
    public static final String MIGRATION_PASS = "migration.jdbc.password";


    private final DatastoreService datastore;
    private DeploymentConfiguration config;
    private AuthenticatedUser user;
    private int databaseId;
    private MigrationFilter filter;

    private Date startDate;
    private int countryId;
    private String databaseName;


    public MigrateDatabaseTask(DeploymentConfiguration config,
                               AuthenticatedUser user) {
        this.config = config;
        this.user = user;
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public void migrate(int databaseId) throws Exception {
        this.databaseId = databaseId;
        try(Connection connection = openConnection()) {

            queryStartDate(connection);
            fetchDatabaseProperties(connection);
            filter = new DatabaseFilter(databaseId, countryId);

            CuidGenerator generator = new CuidGenerator(
                new ClientIdProvider().getNext(), System.currentTimeMillis());


            FreshIdStrategy idStrategy = new FreshIdStrategy(generator);
            ResourceId workspaceId = idStrategy.resourceId(CuidAdapter.DATABASE_DOMAIN, databaseId);
            Workspace workspace = new Workspace(workspaceId);

            LOGGER.info("Workspace id = " + workspaceId);

            MigrationContext context = new MigrationContext(idStrategy, filter);
            context.setRootId(Resources.ROOT_ID);
            context.setGeoDbOwnerId(workspaceId);

            MigrateTransaction tx = new MigrateTransaction(datastore, workspace, user);

            try {
                MySqlMigrator migrator = new MySqlMigrator(context);
                migrator.migrate(connection, new HrdWriter(tx));
                tx.flush();
            } catch(Exception e) {
                LOGGER.log(Level.SEVERE, "Exception whilst migrating database " + databaseName +
                    " [" + databaseId + "]", e);

                // Clean up the entities we've already created, after
                // a suitable delay to make sure indices are caught up
                QueueFactory.getDefaultQueue().add(TaskOptions.Builder
                    .withCountdownMillis(20 * 1000)
                    .url("/service/migrate/tasks/cleanup")
                    .param("workspaceId", workspaceId.asString()));
            }
        }
    }

    private Connection openConnection()  {
        String connectionUrl = Preconditions.checkNotNull(config.getProperty(MIGRATION_SOURCE_URL), MIGRATION_SOURCE_URL);
        String driverClass = Preconditions.checkNotNull(config.getProperty(MIGRATION_DRIVER_CLASS), MIGRATION_DRIVER_CLASS);
        String username =  Preconditions.checkNotNull(config.getProperty(MIGRATION_USER), MIGRATION_USER);
        String password =  Preconditions.checkNotNull(config.getProperty(MIGRATION_PASS), MIGRATION_PASS);

        try {
            Class.forName(driverClass);
            return DriverManager.getConnection(connectionUrl, username, password);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to open migration source connection", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Query the earliest date of a site creation to use as the date of creation of the
     * database and schema elements since we don't have that recorded in the old database.
     *
     */
    private void queryStartDate(Connection connection) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select min(dateCreated) from site s left join activity a on (a.activityid=s.siteid) " +
                "where a.databaseId = " + databaseId);

            rs.next();
            this.startDate = rs.getDate(1);
        }
    }

    private void fetchDatabaseProperties(Connection connection) throws SQLException {
        try(Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select name, countryId from userdatabase where databaseId  = " +
                databaseId);

            rs.next();
            this.databaseName = rs.getString(1);
            this.countryId = rs.getInt(2);
        }
    }

    private class HrdWriter implements ResourceWriter {

        private final WorkspaceTransaction tx;
        private final Workspace workspace;

        private Resource workspaceResource;

        public HrdWriter(WorkspaceTransaction tx) {
            this.tx = tx;
            this.workspace = tx.getWorkspace();
        }

        @Override
        public void beginResources() throws Exception {

        }

        @Override
        public void writeResource(Resource resource, Date dateCreated, Date dateDeleted) throws Exception {
            // Wait until all the other writes are complete before we write the
            // workspace and it becomes to the user
            if(resource.getId().equals(tx.getWorkspace().getWorkspaceId())) {
                workspaceResource = resource;
            } else {
                workspace.createResource(tx, resource);
            }
        }

        @Override
        public void endResources() throws Exception {
            if(workspaceResource != null) {
                workspace.createWorkspace(tx, workspaceResource);
            }
        }

        @Override
        public void writeUserIndex(Multimap<ResourceId, ResourceId> resources) throws Exception {

        }

        @Override
        public void close() throws Exception {

        }
    }
}
