package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Preconditions;
import org.activityinfo.model.resource.ResourceId;

/**
 * Datastore key of the {@code LatestVersion} entities
 */
public class LatestVersionKey implements WorkspaceEntityGroupKey<LatestVersion> {

    public static final String KIND = "Resource";

    private Key key;

    public LatestVersionKey(WorkspaceEntityGroup entityGroup, ResourceId resourceId) {
        this.key = KeyFactory.createKey(entityGroup.getRootKey(), KIND, resourceId.asString());
    }

    public LatestVersionKey(Key key) {
        checkKey(key);
        this.key = key;
    }

    public static void checkKey(Key key) {
        Preconditions.checkArgument(key.getKind().equals(KIND), "Expected key with kind %s", KIND);
        Preconditions.checkArgument(key.getParent().getKind().equals(WorkspaceEntityGroup.KIND),
            "Expected parent key with kind %s");
    }

    public ResourceId getResourceId() {
        return ResourceId.valueOf(key.getName());
    }

    public WorkspaceEntityGroup getParent() {
        return new WorkspaceEntityGroup(key.getParent());
    }

    @Override
    public WorkspaceEntityGroup getWorkspace() {
        return getParent();
    }

    @Override
    public Key unwrap() {
        return key;
    }

    @Override
    public LatestVersion wrapEntity(Entity entity) {
        return new LatestVersion(entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LatestVersionKey that = (LatestVersionKey) o;

        if (!key.equals(that.key)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }

    @Override
    public String toString() {
        return getParent() + "/LatestVersionKey[" + getResourceId() + "]";
    }

}
