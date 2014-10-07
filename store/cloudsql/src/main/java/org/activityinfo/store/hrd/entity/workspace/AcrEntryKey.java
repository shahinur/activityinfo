package org.activityinfo.store.hrd.entity.workspace;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.search.checkers.Preconditions;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.ResourceId;

/**
 * Datastore key for ACR Entries
 */
public class AcrEntryKey implements WorkspaceEntityGroupKey<AcrEntry> {

    public static final String KIND = "ACR";

    private final Key key;

    public AcrEntryKey(Key key) {
        this.key = key;
    }

    public AcrEntryKey(LatestVersionKey parent, ResourceId principalId) {
        this.key = KeyFactory.createKey(parent.unwrap(), KIND, principalId.asString());
    }

    public AcrEntryKey(LatestVersionKey parent, AuthenticatedUser user) {
        this(parent, user.getUserResourceId());
    }

    public AcrEntryKey(WorkspaceEntityGroup workspace, ResourceId resourceId, AuthenticatedUser user) {
        this(new LatestVersionKey(workspace, resourceId), user);
    }

    public static void checkKey(Key key) {
        Preconditions.checkArgument(key.getKind().equals(KIND), "Expected entity of kind %s, found: %s", KIND, key.toString());
        LatestVersionKey.checkKey(key);
    }

    /**
     * Constructs the ACR key for the given user's access to the workspace.
     */
    public AcrEntryKey(WorkspaceEntityGroup workspace, AuthenticatedUser user) {
        this(new LatestVersionKey(workspace, workspace.getWorkspaceId()), user);
    }

    public ResourceId getPrincipalId() {
        return ResourceId.valueOf(key.getName());
    }

    public LatestVersionKey getParent() {
        return new LatestVersionKey(key.getParent());
    }

    @Override
    public Key unwrap() {
        return key;
    }

    @Override
    public AcrEntry wrapEntity(Entity entity) {
        return new AcrEntry(this, entity);
    }

    @Override
    public WorkspaceEntityGroup getWorkspace() {
        return getParent().getParent();
    }
}
