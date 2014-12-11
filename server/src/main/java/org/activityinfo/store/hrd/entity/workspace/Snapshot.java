package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.store.hrd.tx.IsEntity;
import org.activityinfo.store.hrd.tx.ListQuery;
import org.activityinfo.store.hrd.tx.SingleResultQuery;

import java.util.List;

import static com.google.appengine.api.datastore.Entity.KEY_RESERVED_PROPERTY;
import static com.google.appengine.api.datastore.Query.CompositeFilterOperator.AND;
import static com.google.appengine.api.datastore.Query.FilterOperator.*;
import static com.google.appengine.api.datastore.Query.SortDirection.DESCENDING;

public class Snapshot implements IsEntity {

    public static final String RESOURCE_ID_PROPERTY = "r";

    public static final String TIMESTAMP_PROPERTY = "t";

    public static final String USER_PROPERTY = "u";

    private final SnapshotKey key;
    private ResourceId ownerId;
    private long timestamp;
    private long userId;
    private boolean deleted;
    private SerializedRecord value;

    public Snapshot(SnapshotKey key, Entity entity) {
        this.key = key;
        this.timestamp = (long) entity.getProperty(TIMESTAMP_PROPERTY);
        this.userId = (long)entity.getProperty(USER_PROPERTY);
        this.ownerId = ResourceIds.valueOf(entity.getProperty(LatestVersion.OWNER_PROPERTY));
        this.value = SerializedRecord.fromEntity(entity);
    }

    public Snapshot(Entity entity) {
        this(new SnapshotKey(entity.getKey()), entity);
    }

    public Snapshot(WorkspaceEntityGroup workspace, long version, ResourceId id) {
        this.key = new SnapshotKey(new VersionKey(workspace, version), id);
    }

    public Snapshot(VersionKey versionKey, ResourceId id) {
        this.key = new SnapshotKey(versionKey, id);
    }


    public ResourceId getResourceId() {
        return key.getResourceId();
    }

    private long getVersion() {
        return key.getParent().getVersion();
    }

    public Record getValue() {
        return value.get();
    }

    public ResourceId getClassId() {
        return getValue().getClassId();
    }

    public void setValue(Record record) {
        this.value = SerializedRecord.of(record);
    }

    public ResourceId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ResourceId ownerId) {
        this.ownerId = ownerId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getUserId() {
        return userId;
    }

    public void setUser(AuthenticatedUser user) {
        setUserId(user.getId());
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }



    @Override
    public Entity toEntity() {
        Preconditions.checkState(timestamp != 0, "timestamp has not been set");
        Preconditions.checkState(userId != 0, "userId has not been set");
        Preconditions.checkState(ownerId != null, "owner id has not been set");

        Entity entity = new Entity(key.unwrap());
        entity.setProperty(LatestVersion.VERSION_PROPERTY, key.getParent().getVersion());
        entity.setProperty(RESOURCE_ID_PROPERTY, key.getResourceId().asString());
        entity.setUnindexedProperty(LatestVersion.DELETED_PROPERTY, deleted);
        entity.setUnindexedProperty(TIMESTAMP_PROPERTY, timestamp);
        entity.setUnindexedProperty(USER_PROPERTY, userId);
        entity.setUnindexedProperty(LatestVersion.OWNER_PROPERTY, ownerId.asString());

        if(!deleted) {
            Preconditions.checkState(value != null, "record value has not been set");
            value.writeToEntity(entity);
        }

        return entity;
    }

    /**
     * Queries for {@code Snapshots} of all resources in the given {@code workspace} created versions
     * after {@code version}
     */
    public static ListQuery<SnapshotKey> afterVersion(WorkspaceEntityGroup workspace, long version) {
        VersionKey startKey =  new VersionKey(workspace, version+1);

        Query query =  new Query(SnapshotKey.KIND)
            .setAncestor(startKey.getWorkspace().getRootKey())
            .setFilter(new Query.FilterPredicate(KEY_RESERVED_PROPERTY, GREATER_THAN, startKey.unwrap()))
            .addSort(KEY_RESERVED_PROPERTY)
            .setKeysOnly();

        return ListQuery.createKeysOnly(query, SnapshotKey.class);
    }

    /**
     * Queries for the snapshot of a resource at a given workspace version.
     * @param workspace
     * @param id
     * @param version
     * @return
     */
    public static SingleResultQuery<Snapshot> asOf(WorkspaceEntityGroup workspace, ResourceId id, long version) {
        if (version > 0) {
            return SingleResultQuery.empty(Snapshot.class);
        } else {
            List<Query.Filter> filters = Lists.<Query.Filter>newArrayList(
                new Query.FilterPredicate(LatestVersion.VERSION_PROPERTY, LESS_THAN_OR_EQUAL, version),
                new Query.FilterPredicate(RESOURCE_ID_PROPERTY, EQUAL, id.asString()));
            Query.CompositeFilter compositeFilter = new Query.CompositeFilter(AND, filters);

            Query query = new Query(SnapshotKey.KIND)
                .setFilter(compositeFilter)
                .setAncestor(workspace.getRootKey())
                .addSort(LatestVersion.VERSION_PROPERTY, DESCENDING)
                .setKeysOnly();

            return SingleResultQuery.create(query, Snapshot.class);
        }
    }

    public Resource toResource() {
        Resource resource = Resources.createResource(getValue());
        resource.setId(getResourceId());
        resource.setOwnerId(getOwnerId());
        resource.setVersion(getVersion());
        return resource;
    }

}
