package org.activityinfo.store.migrate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.appengine.api.datastore.*;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.json.ObjectMapperFactory;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.service.DeploymentConfiguration;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.store.hrd.entity.ReadTransaction;
import org.activityinfo.store.hrd.entity.UpdateTransaction;
import org.activityinfo.store.hrd.entity.Workspace;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;
import org.activityinfo.store.hrd.index.WorkspaceLookup;

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

    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final DeploymentConfiguration config;

    private final ObjectMapper objectMapper = ObjectMapperFactory.get();

    private ResourceId folderId;
    private int databaseId;

    public MigrateResourceTableTask(DeploymentConfiguration config) {
        this.config = config;
    }

    public void setFolderId(ResourceId folderId) {
        this.folderId = folderId;
    }

    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }

    public int run() {

        WorkspaceLookup workspaceLookup = new WorkspaceLookup();

        Workspace workspace;
        try {
            workspace = workspaceLookup.lookup(folderId);
        } catch(ResourceNotFound e) {
            LOGGER.log(Level.SEVERE, "Folder " + folderId + " does not exist.");
            return 0;
        }

        int count = 0;

        try(Connection connection = openConnection()) {

            long lastMigratedVersion = queryMigratedVersion(workspace);

            LOGGER.log(Level.INFO, "Last version migrated = " + lastMigratedVersion);

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
                if(ownerId.equals(databaseResourceId)) {
                    ownerId = folderId.asString();
                }

                if(update(workspace, ResourceId.valueOf(ownerId), resultSet)) {
                    count++;
                }
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while migrating: " + e.getMessage(), e);
        }

        return count;
    }

    private long queryMigratedVersion(Workspace workspace) {
        try(ReadTransaction tx = new ReadTransaction(workspace, datastore, new AuthenticatedUser(0))) {
            return queryLastMigratedVersion(tx);
        }
    }

    private long queryLastMigratedVersion(WorkspaceTransaction tx) {
        try {
            Entity entity = datastore.get(migratedVersionKey(tx.getWorkspace()));
            return (Long)entity.getProperty("Version");
        } catch (EntityNotFoundException e) {
            return 0L;
        }
    }


    private boolean update(Workspace workspace, ResourceId ownerId, ResultSet resultSet) throws SQLException, IOException {

        ResourceId resourceId = ResourceId.valueOf(resultSet.getString("id"));
        AuthenticatedUser user = getUser(resultSet);
        long sourceVersion = resultSet.getLong("version");

        try(UpdateTransaction tx = new UpdateTransaction(workspace, datastore, user)) {

            // check again, this time within the transaction, that this source version hasn't been applied
            if(sourceVersion <= queryLastMigratedVersion(tx)) {
                return false;
            }

            // use the original timestamp
            Date commitDate = resultSet.getDate("commit_time");
            tx.setTimestamp(commitDate.getTime());

            Resource resource = Resources.createResource();
            resource.setId(resourceId);
            resource.setOwnerId(ownerId);
            resource.setValue(parseRecord(resultSet.getString("content")));

            if (workspace.resourceExists(tx, resourceId)) {
                workspace.updateResource(tx, resource);
            } else {
                workspace.createResource(tx, resource);
            }

            // Update our marker
            Entity migratedVersion = new Entity(migratedVersionKey(workspace));
            migratedVersion.setUnindexedProperty("Version", resultSet.getLong("version"));
            tx.put(migratedVersion);
            tx.commit();

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

    private Key migratedVersionKey(Workspace workspace) {
        return KeyFactory.createKey(workspace.getRootKey(), "MigratedVersion", "Version");
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
