package org.activityinfo.store.migrate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.auth.UserPermission;
import org.activityinfo.model.auth.UserPermissionClass;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.Folder;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.store.hrd.StoreContext;
import org.activityinfo.store.hrd.auth.NullAuthorizer;
import org.activityinfo.store.hrd.dao.Clock;
import org.activityinfo.store.hrd.dao.WorkspaceCreation;
import org.activityinfo.store.hrd.dao.WorkspaceUpdate;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadTx;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Migrates resources from the initial MySQL implementation of the resource store.
 */
public class MigrateResourceTableTask {


    static final String DRIVER_CLASS_PROPERTY = "hibernate.connection.driver_class";
    static final String CONNECTION_URL_PROPERTY = "hibernate.connection.url";

    private static final Logger LOGGER = Logger.getLogger(MigrateResourceTableTask.class.getName());

    private final DeploymentConfiguration config;

    private final ObjectMapper objectMapper = ObjectMapperFactory.get();

    private int databaseId;
    private WorkspaceEntityGroup workspace;
    private StoreContext context = new StoreContext();

    public MigrateResourceTableTask(DeploymentConfiguration config) {
        this.config = config;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
        this.workspace = new WorkspaceEntityGroup(CuidAdapter.databaseId(databaseId));
    }


    public int run() {


        int count = 0;

        try(Connection connection = openConnection()) {

            WorkspaceEntityGroup workspace = new WorkspaceEntityGroup(CuidAdapter.databaseId(databaseId));
            long lastMigratedVersion = queryLastMigratedVersion(workspace);

            LOGGER.log(Level.INFO, "Last version migrated = " + lastMigratedVersion);

            AuthenticatedUser dbOwner = queryOwner(connection);

            if(lastMigratedVersion == 0) {
                createWorkspace(dbOwner, connection);
            }

            writeAcrs(dbOwner, connection);

            PreparedStatement statement = connection.prepareStatement(
                    "select * from resource_version" +
                            " where (owner_id = ? or owner_id in (select id from resource where owner_id = ?)) " +
                            " and version > ? " +
                            " order by version");

            String databaseResourceId = "d" + databaseId;
            statement.setString(1, databaseResourceId);
            statement.setString(2, databaseResourceId);
            statement.setLong(3, lastMigratedVersion);

            ResultSet resultSet = statement.executeQuery();

            while(resultSet.next()) {

                LOGGER.info("Migrating " + resultSet.getString("id")  + " version " + resultSet.getLong("version"));

                String ownerId = resultSet.getString("owner_id");

                if(update(workspace, ResourceId.valueOf(ownerId), resultSet)) {
                    count++;
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while migrating: " + e.getMessage(), e);
        }

        return count;
    }

    private void writeAcrs(AuthenticatedUser owner, Connection connection) throws SQLException {
        try( PreparedStatement query = connection.prepareStatement(
                "select * from userpermission where databaseid = ? order by version")) {

            query.setInt(1, databaseId);

            try( ResultSet resultSet = query.executeQuery() ) {
                while (resultSet.next()) {

                    try (WorkspaceUpdate update = WorkspaceUpdate.newBuilder(context, workspace, owner)
                            .setClock(new HistoricalClock(resultSet.getLong("version")))
                            .begin()) {

                        AuthenticatedUser user = new AuthenticatedUser(resultSet.getInt("userId"));
                        UserPermission rule = new UserPermission(workspace.getWorkspaceId(), user.getUserResourceId());
                        rule.setOwner(false);
                        rule.setDesign(resultSet.getBoolean("AllowDesign"));
                        rule.setView(resultSet.getBoolean("AllowView"));
                        rule.setViewAll(resultSet.getBoolean("AllowViewAll"));
                        rule.setEdit(resultSet.getBoolean("AllowEdit"));
                        rule.setEditAll(resultSet.getBoolean("AllowEditAll"));
                        rule.setManageUsers(resultSet.getBoolean("AllowManageUsers"));
                        rule.setManageAllUsers(resultSet.getBoolean("AllowManageAllUsers"));
                        rule.setUserGroup(CuidAdapter.partnerInstanceId(databaseId, resultSet.getInt("partnerId")));

                        System.out.println(rule);

                        Resource resource = Resources.createResource();
                        resource.setId(rule.getId());
                        resource.setValue(UserPermissionClass.INSTANCE.toRecord(rule));
                        resource.setOwnerId(workspace.getWorkspaceId());

                        update.createResource(resource);
                        update.commit();
                    }
                }
            }
        }
    }

    private void createWorkspace(AuthenticatedUser dbOwner, Connection connection) throws SQLException {

        String databaseName;
        String fullName;
        try( PreparedStatement ownerQuery = connection.prepareStatement(
                "select name, fullName from userdatabase where databaseid = ?")) {
            ownerQuery.setInt(1, databaseId);
            try( ResultSet rs = ownerQuery.executeQuery()) {
                Preconditions.checkState(rs.next());
                databaseName = rs.getString("name");
                fullName = rs.getString("fullName");
            }
        }

        Folder folder = new Folder();
        folder.setLabel(databaseName);
        folder.setDescription(fullName);

        Resource db = Resources.createResource();
        db.setId(workspace.getWorkspaceId());
        db.setOwnerId(Resources.ROOT_ID);
        db.setValue(folder.asRecord());

        WorkspaceCreation creation = new WorkspaceCreation(context, dbOwner);
        creation.createWorkspace(db);
    }

    private AuthenticatedUser queryOwner(Connection connection) throws SQLException {
        try( PreparedStatement ownerQuery = connection.prepareStatement(
                "select name, ownerUserId from userdatabase where databaseid = ?")) {
            ownerQuery.setInt(1, databaseId);
            try( ResultSet rs = ownerQuery.executeQuery()) {
                Preconditions.checkState(rs.next());
                int userId = rs.getInt("ownerUserId");
                return new AuthenticatedUser(userId);
            }
        }
    }

    private long queryLastMigratedVersion(WorkspaceEntityGroup workspace) {
        try(ReadTx tx = ReadTx.withSerializableConsistency()) {
            return queryLastMigratedVersion(workspace, tx);
        }
    }

    private long queryLastMigratedVersion(WorkspaceEntityGroup workspace, ReadTx tx) {
        Optional<MigrationStatus> status = tx.getIfExists(new MigrationStatusKey(workspace));
        if (status.isPresent()) {
            return status.get().getSourceVersionMigrated();
        } else {
            return 0L;
        }
    }

    private static class HistoricalClock implements Clock {

        private final long time;

        public HistoricalClock(Date commitDate) {
            this.time = commitDate.getTime();
        }

        public HistoricalClock(long time) {
            this.time = time;
        }

        @Override
        public long getTime() {
            return time;
        }
    }

    private boolean update(WorkspaceEntityGroup workspace, ResourceId ownerId, ResultSet resultSet) throws SQLException, IOException {

        ResourceId resourceId = ResourceId.valueOf(resultSet.getString("id"));
        AuthenticatedUser user = getUser(resultSet);
        long sourceVersion = resultSet.getLong("version");


        // use the original timestamp
        Date commitDate = resultSet.getDate("commit_time");
        Clock sourceClock = new HistoricalClock(commitDate);

        try(WorkspaceUpdate update = WorkspaceUpdate.newBuilder(context, workspace, user)
                .setClock(sourceClock)
                .setAuthorizer(new NullAuthorizer())
                .begin()) {

            // check again, this time within the transaction, that this source version hasn't been applied
            if(sourceVersion <= queryLastMigratedVersion(workspace)) {
                return false;
            }

            Resource resource = Resources.createResource();
            resource.setId(resourceId);
            resource.setOwnerId(ownerId);
            resource.setValue(parseRecord(resultSet.getString("content")));

            update.createOrUpdateResource(resource);

            // Update our marker
            MigrationStatus status = new MigrationStatus(workspace);
            status.setSourceVersionMigrated(resultSet.getLong("version"));
            update.getTx().put(status);

            update.commit();
            return true;
        }
    }

    private AuthenticatedUser getUser(ResultSet resultSet) throws SQLException {
        String userId = resultSet.getString("user_id");
        if(!userId.startsWith("U")) {
            throw new IllegalStateException("Unexpected user id " + userId);
        }
        return new AuthenticatedUser(Integer.parseInt(userId.substring(1)));
    }

    @VisibleForTesting
    Record parseRecord(String json) throws IOException {
        Resource resource = objectMapper.readValue(json, Resource.class);
        return resource.getValue();
    }


    private java.sql.Connection openConnection() throws ClassNotFoundException, SQLException {
        String driverClass = Preconditions.checkNotNull(config.getProperty(DRIVER_CLASS_PROPERTY), DRIVER_CLASS_PROPERTY);
        String connectionUrl = Preconditions.checkNotNull(config.getProperty(CONNECTION_URL_PROPERTY), CONNECTION_URL_PROPERTY);

        Class.forName(driverClass);
        return DriverManager.getConnection(connectionUrl);
    }


}
