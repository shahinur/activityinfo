package org.activityinfo.store.migrate;

import com.google.common.collect.Multimap;
import org.activityinfo.migrator.MySqlMigrator;
import org.activityinfo.migrator.ResourceWriter;
import org.activityinfo.migrator.filter.DatabaseFilter;
import org.activityinfo.migrator.filter.FreshIdStrategy;
import org.activityinfo.migrator.filter.MigrationContext;
import org.activityinfo.migrator.filter.MigrationFilter;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.store.hrd.HrdResourceStore;

import javax.inject.Provider;
import java.sql.*;
import java.util.Date;

public class MigrateDatabaseTask {

    private HrdResourceStore resourceStore;
    private AuthenticatedUser user;
    private Provider<Connection> connectionProvider;
    private int databaseId;
    private MigrationFilter filter;

    private Date startDate;
    private int countryId;
    private String databaseName;


    public MigrateDatabaseTask(AuthenticatedUser user,
                               HrdResourceStore resourceStore,
                               Provider<Connection> connectionProvider) {
        this.resourceStore = resourceStore;
        this.user = user;
        this.connectionProvider = connectionProvider;
    }

    public void migrate(int databaseId) throws Exception {
        this.databaseId = databaseId;
        try(Connection connection = connectionProvider.get()) {

            queryStartDate(connection);
            fetchDatabaseProperties(connection);
            filter = new DatabaseFilter(databaseId, countryId);

            FreshIdStrategy idStrategy = new FreshIdStrategy();
            ResourceId workspaceId = idStrategy.resourceId(CuidAdapter.DATABASE_DOMAIN, databaseId);

            MigrationContext context = new MigrationContext(idStrategy, filter);
            context.setRootId(Resources.ROOT_ID);
            context.setGeoDbOwnerId(workspaceId);

            MySqlMigrator migrator = new MySqlMigrator(context);
            migrator.migrate(connection, new HrdWriter());
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
            ResultSet rs = statement.executeQuery("select name, countryId from userdatabase where databaseId  = " + databaseId);

            rs.next();
            this.databaseName = rs.getString(1);
            this.countryId = rs.getInt(2);
        }
    }

    private class HrdWriter implements ResourceWriter {

        @Override
        public void beginResources() throws Exception {

        }

        @Override
        public void writeResource(Resource resource, Date dateCreated, Date dateDeleted) throws Exception {
            System.out.println(resource.getId());
            resourceStore.create(user, resource);
        }

        @Override
        public void endResources() throws Exception {

        }

        @Override
        public void writeUserIndex(Multimap<ResourceId, ResourceId> resources) throws Exception {

        }

        @Override
        public void close() throws Exception {

        }
    }
}
