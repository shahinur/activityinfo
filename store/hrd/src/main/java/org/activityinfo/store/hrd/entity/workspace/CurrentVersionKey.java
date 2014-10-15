package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import org.activityinfo.model.resource.ResourceId;

import javax.annotation.Nonnull;

/**
 * Datastore key for the {@link org.activityinfo.store.hrd.entity.workspace.CurrentVersion} entity
 */
public class CurrentVersionKey implements WorkspaceEntityGroupKey<CurrentVersion> {
    public static final String KIND = "WV";
    public static final String KEY_NAME = "current";

    private final WorkspaceEntityGroup workspaceKey;

    public CurrentVersionKey(@Nonnull WorkspaceEntityGroup workspaceKey) {
        this.workspaceKey = workspaceKey;
    }

    public CurrentVersionKey(ResourceId workspaceId) {
        this(new WorkspaceEntityGroup(workspaceId));
    }

    public ResourceId getWorkspaceId() {
        return workspaceKey.getWorkspaceId();
    }

    @Override
    public Key unwrap() {
        return KeyFactory.createKey(workspaceKey.getRootKey(), KIND, KEY_NAME);
    }

    @Override
    public CurrentVersion wrapEntity(Entity entity) {
        return new CurrentVersion(this, entity);
    }

    @Override
    public WorkspaceEntityGroup getWorkspace() {
        return getWorkspace();
    }
}
