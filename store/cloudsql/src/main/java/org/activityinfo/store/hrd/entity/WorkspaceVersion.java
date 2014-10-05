package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Entity;

/**
 * Datastore entity which holds the current version of the Workspace.
 *
 * There is one {@code WorkspaceVersion} entity per workspace.
 */
public class WorkspaceVersion implements IsEntity {

    private static final long INITIAL_VERSION = 0L;

    private static final String VERSION_PROPERTY = "v";

    private final WorkspaceVersionKey key;

    private long currentVersion;

    public WorkspaceVersion(WorkspaceVersionKey key) {
        this.key = key;
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
        Entity entity = new Entity(key.create());
        entity.setUnindexedProperty(VERSION_PROPERTY, currentVersion);
        return entity;
    }
}
