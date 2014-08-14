package org.activityinfo.store.cloudsql;

import com.google.common.base.Preconditions;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.system.FolderClass;
import org.activityinfo.service.store.CommitStatus;
import org.activityinfo.service.store.UpdateResult;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Updates the ResourceStore to a new version
 */
public class ResourceUpdate {

    private static final Logger LOGGER = Logger.getLogger(ResourceUpdate.class.getName());

    private final StoreConnection connection;
    private final StoreCache cache;

    private ResourceId userId;
    private Resource resource;
    private long newVersion;


    public ResourceUpdate(StoreConnection connection, StoreCache cache) {
        this.connection = connection;
        this.cache = cache;
    }

    public ResourceUpdate create(Resource resource) {
        this.resource = resource;
        return this;
    }


    public ResourceUpdate update(Resource resource) {
        this.resource = resource;
        return this;
    }

    public ResourceUpdate byUser(ResourceId userId) {
        this.userId = userId;
        return this;
    }


    public UpdateResult execute() throws SQLException {

        newVersion = incrementGlobalVersion();

        // stmt first into the resource_version table
        updateResourceVersionTable();

        // stmt or replace now the latest version record
        updateResourceTable();

        // update the sub_tree_version of all parents
        updateOwnerSubTreeVersions();

        // commit the transaction
        connection.commit();

        return new UpdateResult(CommitStatus.COMMITTED, newVersion);
    }

    /**
     * Increments the global version number and returns the result.
     */
    public long incrementGlobalVersion() throws SQLException {

        int rowsAffected = connection.executeUpdate(
                "UPDATE global_version SET current_version = current_version + 1");

        Preconditions.checkState(rowsAffected == 1, "global_version must contain exactly one row");

        long newVersion = connection.queryLong("SELECT current_version FROM global_version").get();

        return newVersion;
    }

    /**
     * Updates the {@code resource} table, which contains the latest version of the resource and maintains
     * keys used for caching.
     *
     */
    private void updateResourceTable() throws SQLException {
        try(PreparedStatement stmt = connection.prepareStatement(
            "INSERT INTO resource (id, version, owner_id, class_id, label, sub_tree_version, content) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)" +
            "ON DUPLICATE KEY UPDATE " +
                "version = VALUES(version), " +
                "label = VALUES(label), " +
                "sub_tree_version = VALUES(sub_tree_version), " +
                "content = VALUES(content)")) {

            stmt.setString(1, resource.getId().asString());
            stmt.setLong(  2, newVersion);
            stmt.setString(3, resource.getOwnerId().asString());
            stmt.setString(4, resource.isString("classId"));
            stmt.setString(5, getLabel(resource));
            stmt.setLong(  6, newVersion);
            stmt.setString(7, Resources.toJson(resource));

            stmt.executeUpdate();
        }
    }


    /**
     * Updates the {@code resource_version} table, which maintains a trace of all versions
     * of resources.
     */
    private void updateResourceVersionTable() throws SQLException {

        // TODO: if the parent has changed, we have to update the old parent too

        try(PreparedStatement stmt = connection.prepareStatement(
            "INSERT INTO resource_version (id, version, owner_id, user_id, content) " +
            "VALUES (?, ?, ?, ?, ?)")) {

            stmt.setString(1, resource.getId().asString());
            stmt.setLong(2, newVersion);
            stmt.setString(3, resource.getOwnerId().asString());
            stmt.setString(4, userId.asString());
            stmt.setString(5, Resources.toJson(resource));
            stmt.executeUpdate();
        }
    }

    private void updateOwnerSubTreeVersions() throws SQLException {

        try(PreparedStatement ownerUpdate = connection.prepareStatement(
                "UPDATE resource SET sub_tree_version = ? WHERE id = ?");
            PreparedStatement ownerQuery = connection.prepareStatement(
                "SELECT owner_id FROM resource WHERE id = ?")) {

            ResourceId ownerId = resource.getOwnerId();
            while (!ownerId.equals(ResourceId.ROOT_ID)) {

                LOGGER.fine("Updating owner [" + ownerId.asString() + "] to sub_tree_version " + newVersion);

                // Update the database record
                ownerUpdate.setLong(1, newVersion);
                ownerUpdate.setString(2, ownerId.asString());
                ownerUpdate.addBatch();

                // Update memcache
                cache.subTreeVersion(ownerId).put(newVersion);

                // Fetch this owner's owner
                ownerQuery.setString(1, ownerId.asString());
                try (ResultSet rs = ownerQuery.executeQuery()) {
                    if (!rs.next()) {
                        LOGGER.fine("Owner [" + ownerId.asString() + "] no longer exists");

                        // owners can get deleted
                        break;
                    }
                    ownerId = ResourceId.valueOf(rs.getString(1));
                }
            }

            ownerUpdate.executeBatch();
        }
    }

    private String getLabel(Resource resource) {
        if(resource.has("classId")) {
            ResourceId classId = ResourceId.valueOf(resource.getString("classId"));
            if(classId.equals(FormClass.CLASS_ID)) {
                return resource.getString(FormClass.LABEL_FIELD_ID);
            } else if(classId.equals(FolderClass.CLASS_ID)) {
                return resource.getString(FolderClass.LABEL_FIELD_ID.asString());
            }
        }
        return null;
    }

}
