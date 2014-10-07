package org.activityinfo.store.hrd.entity.user;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.IsEntity;
import org.activityinfo.store.hrd.tx.ListQuery;

/**
 * Datastore entity that links a user to a Workspace they own,
 * or have been shared.
 */
public class UserWorkspace implements IsEntity {


    public static final String OWNED_PROPERTY = "own";

    private final UserWorkspaceKey key;
    private boolean owned;


    public UserWorkspace(UserWorkspaceKey key, Entity entity) {
        this.key = key;
        this.owned = (entity.getProperty(OWNED_PROPERTY) == Boolean.TRUE);
    }

    public UserWorkspace(AuthenticatedUser user, WorkspaceEntityGroup workspace) {
        this.key = new UserWorkspaceKey(new UserEntityGroup(user), workspace.getWorkspaceId());
    }

    public UserWorkspaceKey getKey() {
        return key;
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    @Override
    public Entity toEntity() {
        Entity entity = new Entity(key.unwrap());

        if(owned) {
            entity.setProperty(OWNED_PROPERTY, owned);
        }
        return entity;
    }

    public static ListQuery<UserWorkspaceKey> ofUser(AuthenticatedUser user) {
        Query query = new Query(UserWorkspaceKey.KIND)
            .setAncestor(new UserEntityGroup(user).getRootKey())
            .setKeysOnly();

        return ListQuery.createKeysOnly(query, UserWorkspaceKey.class);
    }
}
