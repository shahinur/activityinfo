package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.tx.IsEntity;
import org.activityinfo.store.hrd.tx.ListQuery;

/**
 * Datastore entity that stores Access Control Rules
 */
public class AcrEntry implements IsEntity {

    public static final String OWNER_PROPERTY = "owner";
    public static final String VERSION_PROPERTY = "version";

    private final AcrEntryKey key;
    private boolean owner;
    private long version;
    private SerializedRecord record;

    public AcrEntry(AcrEntryKey key, Entity entity) {
        this.key = key;
        this.owner = (Boolean)entity.getProperty(OWNER_PROPERTY);

        if(!this.owner) {
            this.record = Preconditions.checkNotNull(SerializedRecord.fromEntity(entity));
        }

        if (entity.hasProperty(VERSION_PROPERTY)) {
            this.version = (Long)entity.getProperty(VERSION_PROPERTY);
        } else {
            // version was not stored earlier, but in those cases
            // all should be for the workspace owner, created in version 1
            this.version = 1L;
        }
    }

    public AcrEntry(Entity entity) {
        this(new AcrEntryKey(entity.getKey()), entity);
    }

    public AcrEntry(WorkspaceEntityGroup workspace, ResourceId id, AuthenticatedUser user) {
        this.key = new AcrEntryKey(new LatestVersionKey(workspace, id), user.getUserResourceId());
    }

    public AcrEntry(WorkspaceEntityGroup workspace, AccessControlRule rule) {
        this.key = new AcrEntryKey(new LatestVersionKey(workspace, rule.getResourceId()), rule.getPrincipalId());
        this.owner = rule.isOwner();
        if(!this.owner) {
            this.record = SerializedRecord.of(rule.asResource().getValue());
        }
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public AccessControlRule toAccessControlRule() {
        return AccessControlRule.fromRecord(
                key.getParent().getResourceId(),
                key.getPrincipalId(),
                record.get());
    }

    @Override
    public Entity toEntity() {
        Entity entity = new Entity(key.unwrap());
        entity.setUnindexedProperty(OWNER_PROPERTY, owner);
        entity.setUnindexedProperty(VERSION_PROPERTY, version);
        if(!owner) {
            record.writeToEntity(entity);
        }
        return entity;
    }

    public static ListQuery<Resource> forResource(LatestVersionKey latestVersion) {
        return new ListQuery<>(new Query(AcrEntryKey.KIND, latestVersion.unwrap()), new Function<Entity, Resource>() {

            @Override
            public Resource apply(Entity input) {
                return new AcrEntry(input).toAccessControlRule().asResource();
            }
        });
    }
}
