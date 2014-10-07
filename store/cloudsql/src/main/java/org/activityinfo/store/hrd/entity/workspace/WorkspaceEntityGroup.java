package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.checkers.Preconditions;
import org.activityinfo.model.resource.ResourceId;

import javax.annotation.Nonnull;

/**
 * The datastore key for the root of the workspace entity group.
 *
 */
public class WorkspaceEntityGroup {

    public static final String KIND = "W";

    private final ResourceId workspaceId;

    public WorkspaceEntityGroup(ResourceId workspaceId) {
        this.workspaceId = workspaceId;
    }

    public WorkspaceEntityGroup(@Nonnull Key key) {
        checkKey(key);
        this.workspaceId = ResourceId.valueOf(key.getName());
    }

    public static void checkKey(Key key) {
        Preconditions.checkArgument(key.getKind().equals(KIND), "Expected kind %s, found %s", KIND, key.toString());
        Preconditions.checkArgument(key.getParent() == null, "Expected root key, found %s", key.toString());
    }

    public ResourceId getWorkspaceId() {
        return workspaceId;
    }

    public Key getRootKey() {
        return KeyFactory.createKey(KIND, workspaceId.asString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WorkspaceEntityGroup that = (WorkspaceEntityGroup) o;

        if (!workspaceId.equals(that.workspaceId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return workspaceId.hashCode();
    }


    @Override
    public String toString() {
        return "Workspace[" + getWorkspaceId() + "]";
    }
}
