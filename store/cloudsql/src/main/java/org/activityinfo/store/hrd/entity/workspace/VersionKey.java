package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Preconditions;
import org.activityinfo.store.hrd.tx.IsEntity;

/**
 * Parent key of {@code Snapshots}
 *
 */
public class VersionKey implements WorkspaceEntityGroupKey {

    public static final String KIND = "V";

    private final Key key;

    public VersionKey(WorkspaceEntityGroup workspaceKey, long version) {
        this.key = KeyFactory.createKey(workspaceKey.getRootKey(), KIND, version);
    }

    public VersionKey(Key key) {
        checkKey(key);
        this.key = key;
    }

    public static void checkKey(Key key) {
        Preconditions.checkArgument(key.getKind().equals(KIND), "Expected key of kind %s", KIND);
        WorkspaceEntityGroup.checkKey(key.getParent());
    }

    public long getVersion() {
        return key.getId();
    }

    @Override
    public Key unwrap() {
        return key;
    }

    @Override
    public IsEntity wrapEntity(Entity entity) {
        throw new UnsupportedOperationException("Version Key has no corresponding entity, it is used only as a part of Snapshots");
    }

    @Override
    public WorkspaceEntityGroup getWorkspace() {
        return new WorkspaceEntityGroup(key.getParent());
    }
}
