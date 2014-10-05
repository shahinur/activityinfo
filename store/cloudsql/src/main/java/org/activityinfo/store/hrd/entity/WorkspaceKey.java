package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.checkers.Preconditions;
import org.activityinfo.model.resource.ResourceId;

import javax.annotation.Nonnull;

/**
 * The datastore key for the root of the workspace entity group.
 *
 */
public class WorkspaceKey implements IsKey {

    public static final String KIND = Workspace.ROOT_KIND;

    private final ResourceId workspaceId;

    public WorkspaceKey(ResourceId workspaceId) {
        this.workspaceId = workspaceId;
    }

    public WorkspaceKey(@Nonnull Key key) {
        Preconditions.checkArgument(key.getKind().equals(KIND) && key.getParent() == null, key.toString());

        this.workspaceId = ResourceId.valueOf(key.getName());
    }

    public ResourceId getWorkspaceId() {
        return workspaceId;
    }

    @Override
    public Key create() {
        return KeyFactory.createKey(KIND, workspaceId.asString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkspaceKey that = (WorkspaceKey) o;

        if (!workspaceId.equals(that.workspaceId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return workspaceId.hashCode();
    }
}
