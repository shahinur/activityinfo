package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.store.hrd.tx.IsEntity;
import org.activityinfo.store.hrd.tx.ListQuery;
import org.activityinfo.store.hrd.tx.SingleResultQuery;

import static com.google.appengine.api.datastore.Query.FilterOperator.EQUAL;

/**
 * Datastore entity that stores the latest version of a Resource.
 */
public class LatestVersion implements IsEntity, ResourceVersion {

    public static final String RESOURCE_ID_PROPERTY = "ID";
    public static final String ROW_INDEX_PROPERTY = "R";
    public static final String TX_ID = "TX";
    public static final String LABEL_PROPERTY = "L";
    public static final String CLASS_PROPERTY = "C";
    public static final String OWNER_PROPERTY = "O";
    public static final String VERSION_PROPERTY = "V";
    public static final String DELETED_PROPERTY = "D";

    private LatestVersionKey key;
    private ResourceId ownerId;
    private ResourceId classId;
    private boolean deleted;
    private long transactionId;
    private Long version;
    private Long rowIndex;
    private String label;
    private SerializedRecord record;

    public LatestVersion(Entity entity) {
        this.key = new LatestVersionKey(entity.getKey());
        this.deleted = (entity.getProperty(DELETED_PROPERTY) == Boolean.TRUE);
        this.ownerId = ResourceIds.valueOf(entity.getProperty(OWNER_PROPERTY));
        this.classId = ResourceIds.valueOf(entity.getProperty(CLASS_PROPERTY));
        this.label = (String)entity.getProperty(LABEL_PROPERTY);
        this.version = (Long)entity.getProperty(VERSION_PROPERTY);
        this.rowIndex = (Long)entity.getProperty(ROW_INDEX_PROPERTY);
        this.record = SerializedRecord.fromEntity(entity);

        if(entity.getProperty(TX_ID) != null) {
            this.transactionId = (long)entity.getProperty(TX_ID);
        }
    }

    /**
     * Constructs a new LatestVersion that is a copy of {@code previousVersion}
     */
    public LatestVersion(LatestVersion previousVersion) {
        this(previousVersion.toEntity());
    }

    public LatestVersion(WorkspaceEntityGroup workspace, Resource resource) {
        this.key = new LatestVersionKey(workspace, resource.getId());
        this.classId = resource.getValue().getClassId();
        this.ownerId = resource.getOwnerId();
        this.record = SerializedRecord.of(resource.getValue());
    }


    public LatestVersionKey getKey() {
        return key;
    }

    public ResourceId getResourceId() {
        return key.getResourceId();
    }

    public ResourceId getWorkspaceId() {
        return key.getParent().getWorkspaceId();
    }

    public LatestVersionKey getOwnerLatestVersionKey() {
        return new LatestVersionKey(key.getParent(), getOwnerId());
    }

    public void set(Resource resource) {
        setOwnerId(resource.getOwnerId());
        setClassId(resource.getValue().getClassId());
        setRecord(resource.getValue());
    }

    public ResourceId getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(ResourceId ownerId) {
        this.ownerId = ownerId;
    }

    public ResourceId getClassId() {
        return classId;
    }

    public void setClassId(ResourceId classId) {
        this.classId = classId;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Long rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Record getRecord() {
        return record.get();
    }

    public void setRecord(Record record) {
        this.record = SerializedRecord.of(record);
    }

    @Override
    public Entity toEntity() {

        Preconditions.checkState(ownerId != null, "ownerId is required");
        Preconditions.checkState(version != null && version != 0, "version cannot be zero");

        Entity entity = new Entity(key.unwrap());

        // Version property is indexed so that we can retrieve it
        // using a projection query
        entity.setProperty(VERSION_PROPERTY, version);

        // Store the resourceId as indexed property as well as in this key
        // so that we can query by resourceId to find its workspace
        entity.setProperty(RESOURCE_ID_PROPERTY, key.getResourceId().asString());

        if(this.deleted) {
            // We retain deleted entities but effectively remove them from the
            // indexes so that they are not visible to the relevant queries
            entity.setUnindexedProperty(DELETED_PROPERTY, true);
            entity.setUnindexedProperty(OWNER_PROPERTY, ownerId.asString());
            entity.setUnindexedProperty(ROW_INDEX_PROPERTY, rowIndex);
        } else {

            Preconditions.checkState(record != null, "if not deleted, resource must have record value");
            Preconditions.checkState(record.get().getClassId() != null, "records must have class id");

            entity.setProperty(OWNER_PROPERTY, ownerId.asString());
            entity.setProperty(ROW_INDEX_PROPERTY, rowIndex);
            entity.setProperty(LABEL_PROPERTY, label);
            entity.setProperty(CLASS_PROPERTY, record.getClassId().asString());
            entity.setProperty(TX_ID, transactionId);
            record.writeToEntity(entity);
        }
        return entity;
    }

    public Resource toResource() {
        Resource resource = Resources.createResource();
        resource.setId(getResourceId());
        resource.setOwnerId(getOwnerId());
        resource.setVersion(getVersion());
        resource.setValue(getRecord());
        return resource;
    }

    public static ListQuery<ResourceNode> folderItemsOf(final LatestVersionKey folderKey) {
        Query query = new Query(LatestVersionKey.KIND)
            .setAncestor(folderKey.getWorkspace().getRootKey())
            .addProjection(new PropertyProjection(VERSION_PROPERTY, Long.class))
            .addProjection(new PropertyProjection(LABEL_PROPERTY, String.class))
            .addProjection(new PropertyProjection(CLASS_PROPERTY, String.class))
            .setFilter(ownerIs(folderKey));

        Function<Entity, ResourceNode> transform = new Function<Entity, ResourceNode>() {
            @Override
            public ResourceNode apply(Entity input) {
                LatestVersion latestVersion = new LatestVersion(input);
                ResourceNode node = new ResourceNode(latestVersion.getResourceId());
                node.setOwnerId(folderKey.getResourceId());
                node.setLabel(latestVersion.getLabel());
                node.setClassId(latestVersion.getClassId());
                node.setVersion(latestVersion.getVersion());
                return node;
            }
        };

        return new ListQuery<>(query, transform);
    }

    public static ListQuery<LatestVersionKey> queryChildKeys(LatestVersionKey parentKey) {
        Query query = new Query(LatestVersionKey.KIND)
            .setAncestor(parentKey.getWorkspace().getRootKey())
            .setFilter(ownerIs(parentKey))
            .setKeysOnly();

        return ListQuery.createKeysOnly(query, LatestVersionKey.class);
    }

    public static ListQuery<Resource> formInstancesOf(LatestVersionKey formClassKey) {
        Query query = new Query(LatestVersionKey.KIND)
            .setFilter(ownerIs(formClassKey))
            .addSort(ROW_INDEX_PROPERTY)
            .setAncestor(formClassKey.getWorkspace().getRootKey());

        return new ListQuery<>(query, new Function<Entity, Resource>() {
            @Override
            public Resource apply(Entity input) {
                return new LatestVersion(input).toResource();
            }
        });
    }

    public static SingleResultQuery<LatestVersionKey> ofResource(ResourceId id) {
        Query query = new Query(LatestVersionKey.KIND)
            .setFilter(new Query.FilterPredicate(RESOURCE_ID_PROPERTY, EQUAL, id.asString()))
            .setKeysOnly();

        return SingleResultQuery.createKeysOnly(query, LatestVersionKey.class);
    }

    public static Query.FilterPredicate ownerIs(LatestVersionKey ownerKey) {
        return new Query.FilterPredicate(OWNER_PROPERTY, EQUAL, ownerKey.getResourceId().asString());
    }

}
