package org.activityinfo.store.hrd.entity.user;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.common.base.Preconditions;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.entity.workspace.LatestVersionKey;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.IsKey;

public class UserWorkspaceKey implements IsKey<UserWorkspace> {

    public static final String KIND = "Workspace";

    private final Key key;

    public UserWorkspaceKey(Key key) {
        Preconditions.checkArgument(key.getKind().equals(KIND), "Expected kind " + KIND);
        this.key = key;
    }

    public UserWorkspaceKey(UserEntityGroup entityGroup, ResourceId workspaceId) {
        this.key = KeyFactory.createKey(entityGroup.getRootKey(), KIND, workspaceId.asString());
    }

    public ResourceId getWorkspaceId() {
        return ResourceId.valueOf(key.getName());
    }

    public LatestVersionKey workspaceResourceKey() {
        return new LatestVersionKey(new WorkspaceEntityGroup(getWorkspaceId()), getWorkspaceId());
    }

    @Override
    public Key unwrap() {
        return key;
    }

    @Override
    public UserWorkspace wrapEntity(Entity entity) {
        return new UserWorkspace(this, entity);
    }
}
