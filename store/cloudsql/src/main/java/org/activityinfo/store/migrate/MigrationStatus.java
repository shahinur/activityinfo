package org.activityinfo.store.migrate;

import com.google.appengine.api.datastore.Entity;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.IsEntity;

public class MigrationStatus implements IsEntity {

    public static final String SOURCE_VERSION_PROPERTY = "Version";

    private final MigrationStatusKey key;
    private long sourceVersionMigrated;

    public MigrationStatus(MigrationStatusKey key, Entity entity) {
        this.key = key;
        this.sourceVersionMigrated = (Long)entity.getProperty(SOURCE_VERSION_PROPERTY);
    }

    public MigrationStatus(WorkspaceEntityGroup workspace) {
        this.key = new MigrationStatusKey(workspace);
    }

    public long getSourceVersionMigrated() {
        return sourceVersionMigrated;
    }

    public void setSourceVersionMigrated(long sourceVersionMigrated) {
        this.sourceVersionMigrated = sourceVersionMigrated;
    }

    @Override
    public Entity toEntity() {
        Entity entity = new Entity(key.unwrap());
        entity.setProperty(SOURCE_VERSION_PROPERTY, sourceVersionMigrated);
        return entity;
    }
}
