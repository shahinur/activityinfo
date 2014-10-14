package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import org.activityinfo.store.hrd.tx.IsEntity;

/**
 * Datastore entity which holds the current version of the Workspace.
 *
 * There is one {@code CurrentVersion} entity per workspace.
 */
public class CurrentVersion implements IsEntity {

    private static final String VERSION_PROPERTY = "v";

    private final CurrentVersionKey key;

    private long currentVersion;

    public CurrentVersion(CurrentVersionKey key) {
        this.key = key;
    }

    public CurrentVersion(WorkspaceEntityGroup key) {
        this.key = new CurrentVersionKey(key);
    }

    public CurrentVersion(CurrentVersionKey key, Entity entity) {
        this.key = key;
        this.currentVersion = (Long)entity.getProperty(VERSION_PROPERTY);
    }

    public CurrentVersion(WorkspaceEntityGroup workspace, long updateVersion) {
        this.key = new CurrentVersionKey(workspace);
        this.currentVersion = updateVersion;
    }

    /**
     *
     * @return the current version of the workspace
     */
    public long getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(long currentVersion) {
        this.currentVersion = currentVersion;
    }

    @Override
    public Entity toEntity() {
        Entity entity = new Entity(key.unwrap());
        entity.setUnindexedProperty(VERSION_PROPERTY, currentVersion);
        return entity;
    }
}
