package org.activityinfo.store.hrd.auth2;

import com.google.common.base.Optional;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.auth.UserPermission;
import org.activityinfo.model.auth.UserPermissionClass;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.store.hrd.auth.*;
import org.activityinfo.store.hrd.entity.workspace.*;
import org.activityinfo.store.hrd.tx.ReadableTx;

public class LegacyAuthDAO implements Authorizer {


    private final ReadableTx tx;
    private final WorkspaceEntityGroup workspace;
    private final AuthenticatedUser user;

    private final Optional<AcrEntry> workspaceAcr;
    private Optional<UserPermission> userPermission;

    public LegacyAuthDAO(WorkspaceEntityGroup workspace, AuthenticatedUser user, ReadableTx tx) {
        this.tx = tx;
        this.workspace = workspace;
        this.user = user;

        // Are we the owner?
        workspaceAcr =
                tx.getIfExists(new AcrEntryKey(workspace, user));

        // Retrieve the top level acr if it exists
        ResourceId permissionId = UserPermission.calculateId(workspace.getWorkspaceId(), user.getUserResourceId());
        Optional<LatestVersion> latestVersion = tx.getIfExists(new LatestVersionKey(workspace, permissionId));
        if(latestVersion.isPresent()) {
            userPermission = Optional.of(UserPermissionClass.INSTANCE.toBean(latestVersion.get().getRecord()));
        } else {
            userPermission = Optional.absent();
        }
    }

    @Override
    public Authorization forResource(ResourceId id) {

        if(workspaceAcr.isPresent() && workspaceAcr.get().isOwner()) {
            return new IsOwner();
        }

        // Geographic databases are open to be read by everyone
        if(workspace.getWorkspaceId().getDomain() == CuidAdapter.COUNTRY_DOMAIN) {
            return new ReadAuthorization();
        }

        // Otherwise, in normal user databases, access depends on the
        // old "type" of form - activities, sites, etc.

        LatestVersion resource = tx.getOrThrow(new LatestVersionKey(workspace, id));

        // Accessing UserPermissions themselves have special access rules
        if(resource.getClassId().equals(UserPermissionClass.CLASS_ID)) {
            UserPermission up = UserPermissionClass.INSTANCE.toBean(resource.getRecord());
            if(userPermission.isPresent() && userPermission.get().isOwner()) {
                return new IsOwner();
            } else if(up.getPrincipalId().equals(user.getUserResourceId())) {
                return new ReadAuthorization();
            } else {
                return new NoAuthorization();
            }
        }

        if(userPermission.isPresent()) {
            if (resource.getClassId().getDomain() == CuidAdapter.ACTIVITY_DOMAIN) {
                return new SiteAuthorization(userPermission.get());
            } else {
                return new DesignAuthorization(userPermission.get());
            }
        }
        return new NoAuthorization();
    }
}
