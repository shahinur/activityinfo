package org.activityinfo.store.migrate;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.activityinfo.migrator.MySqlMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.DatabaseFilter;
import org.activityinfo.migrator.filter.LegacyIdStrategy;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.migrator.filter.MigrationFilter;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.store.hrd.HrdResourceStore;
import org.activityinfo.store.hrd.auth.NullAuthorizer;
import org.activityinfo.store.hrd.dao.ConstantClock;
import org.activityinfo.store.hrd.dao.WorkspaceCreation;
import org.activityinfo.store.hrd.dao.WorkspaceUpdate;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;

import java.sql.*;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MigrateDatabaseTask {

    private static final Logger LOGGER = Logger.getLogger(MigrateDatabaseTask.class.getName());

    public static final String MIGRATION_SOURCE_URL = "migration.jdbc.url";
    public static final String MIGRATION_DRIVER_CLASS = "migration.jdbc.driver.class";
    public static final String MIGRATION_USER = "migration.jdbc.username";
    public static final String MIGRATION_PASS = "migration.jdbc.password";


    private HrdResourceStore store;
    private DeploymentConfiguration config;
    private AuthenticatedUser user;
    private int databaseId;
    private MigrationFilter filter;

    private Date startDate;
    private int countryId;
    private String databaseName;

    public MigrateDatabaseTask(HrdResourceStore store,
                               DeploymentConfiguration config,
                               AuthenticatedUser user) {
        this.store = store;
        this.config = config;
        this.user = user;
    }

    public void migrate(int databaseId) throws Exception {
        this.databaseId = databaseId;
        try(Connection connection = openConnection()) {

            queryStartDate(connection);
            fetchDatabaseProperties(connection);
            filter = new DatabaseFilter(databaseId, countryId);

            LegacyIdStrategy idStrategy = new LegacyIdStrategy(new HrdIdStore());
            MigrationContext context = new MigrationContext(idStrategy, filter);
            context.setRootId(Resources.ROOT_ID);

            try {
                MySqlMigrator migrator = new MySqlMigrator(context);
                HrdWriter writer = new HrdWriter();
                migrator.migrate(connection, writer);

            } catch(Exception e) {
                LOGGER.log(Level.SEVERE, "Exception whilst migrating database " + databaseName +
                    " [" + databaseId + "]", e);

//                // Clean up the entities we've already created, after
//                // a suitable delay to make sure indices are caught up
//                QueueFactory.getDefaultQueue().add(TaskOptions.Builder
//                    .withCountdownMillis(20 * 1000)
//                    .url("/service/migrate/tasks/cleanup")
//                    .param("workspaceId", workspaceId.asString()));
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

        private boolean closed = false;

        private Map<ResourceId, WorkspaceEntityGroup> workspaceMap = Maps.newHashMap();
        private Map<ResourceId, AuthenticatedUser> workspaceOwner = Maps.newHashMap();

        @Override
        public void beginResources() throws Exception {

        }

        @Override
        public void writeResource(int userId, Resource resource, Date dateCreated, Date dateDeleted) throws Exception {

            if(resource.getOwnerId().equals(Resources.ROOT_ID)) {
                Preconditions.checkArgument(userId != 0, "workspace " + resource.getId() + " has no user set");
                AuthenticatedUser user = new AuthenticatedUser(userId);
                WorkspaceCreation creation = new WorkspaceCreation(store.getContext(), user);
                creation.createWorkspace(resource);

                workspaceMap.put(resource.getId(), new WorkspaceEntityGroup(resource.getId()));
                workspaceOwner.put(resource.getId(), user);

            } else {

                WorkspaceEntityGroup workspace = workspaceMap.get(resource.getOwnerId());
                Preconditions.checkNotNull(workspace, "No parent found : " + resource.getOwnerId());

                AuthenticatedUser user;
                if(userId != 0) {
                    user = new AuthenticatedUser(userId);
                } else {
                    user = workspaceOwner.get(workspace.getWorkspaceId());
                    Preconditions.checkState(user != null, "no user stored for " + workspace);
                }

                if(dateCreated == null) {
                    dateCreated = new Date();
                }

                try( WorkspaceUpdate update = WorkspaceUpdate.newBuilder(store.getContext())
                                    .setAuthorizer(new NullAuthorizer())
                                    .setClock(new ConstantClock(dateCreated))
                                    .setUser(user)
                                    .setWorkspace(workspace)
                                    .begin()) {

                    update.createOrUpdateResource(resource);
                    update.commit();
                }
                workspaceMap.put(resource.getId(), workspace);
            }
        }

        @Override
        public void endResources() throws Exception {

        }

        @Override
        public void close() throws Exception {
        }

    }
}
