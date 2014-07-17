package org.activityinfo.server.command.handler;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import org.activityinfo.legacy.shared.auth.AuthenticatedUser;
import org.activityinfo.legacy.shared.exception.IllegalAccessCommandException;
import org.activityinfo.server.database.hibernate.entity.Site;
import org.activityinfo.server.database.hibernate.entity.User;
import org.activityinfo.server.database.hibernate.entity.UserDatabase;
import org.activityinfo.server.database.hibernate.entity.UserPermission;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import java.util.List;

public class PermissionOracle {

    private final Provider<EntityManager> em;

    @Inject
    public PermissionOracle(Provider<EntityManager> em) {
        this.em = em;
    }

    public PermissionOracle(EntityManager em) {
        this(Providers.of(em));
    }

    /**
     * Returns true if the given user is allowed to modify the structure of the
     * database.
     */
    public boolean isDesignAllowed(UserDatabase database, User user) {
        return getPermissionByUser(database, user).isAllowDesign();
    }

    public boolean isManageUsersAllowed(UserDatabase database, User user) {
        return getPermissionByUser(database, user).isAllowDesign() ||
               getPermissionByUser(database, user).isAllowManageUsers();
    }


    public boolean isManagePartnersAllowed(UserDatabase db, User user) {
        UserPermission perm = getPermissionByUser(db, user);
        return perm.isAllowDesign() || perm.isAllowManageAllUsers();
    }

    public void assertDesignPrivileges(UserDatabase database, User user) {
        if (!isDesignAllowed(database, user)) {
            throw new IllegalAccessCommandException(String.format(
                    "User %d does not have design privileges on database %d",
                    user.getId(),
                    database.getId()));
        }
    }


    public void assertManagePartnerAllowed(UserDatabase database, User user) {
        if (!isManagePartnersAllowed(database, user)) {
            throw new IllegalAccessCommandException(String.format(
                    "User %d does not have design or manageAllUsers privileges on database %d",
                    user.getId(),
                    database.getId()));
        }
    }

    /**
     * Returns true if the given user is allowed to edit the values of the
     * given site.
     */
    public boolean isEditAllowed(Site site, User user) {
        UserPermission permission = getPermissionByUser(site.getActivity().getDatabase(), user);

        if (permission.isAllowEditAll()) {
            return true;
        }

        if (permission.isAllowEdit()) {
            // without AllowEditAll, edit permission is contingent on the site's partner
            return site.getPartner().getId() == permission.getPartner().getId();
        }

        return false;
    }

    /**
     * Returns true if the given user is allowed to edit the values of the
     * given site.
     */
    public boolean isViewAllowed(Site site, User user) {

        if(site.getActivity().getPublished() == 1) {
            return true;
        }

        UserPermission permission = getPermissionByUser(site.getActivity().getDatabase(), user);

        if (permission.isAllowViewAll()) {
            return true;
        }

        if (permission.isAllowView()) {
            // without AllowViewAll, edit permission is contingent on the site's partner
            return site.getPartner().getId() == permission.getPartner().getId();
        }

        return false;
    }


    public boolean isEditAllowed(Site site, AuthenticatedUser user) {
        return isEditAllowed(site, em.get().getReference(User.class, user.getId()));
    }

    @Nonnull
    public UserPermission getPermissionByUser(UserDatabase database, User user) {

        if (database.getOwner().getId() == user.getId()) {
            // owner has all rights
            UserPermission ownersPermission = new UserPermission();
            ownersPermission.setAllowView(true);
            ownersPermission.setAllowViewAll(true);
            ownersPermission.setAllowDesign(true);
            ownersPermission.setAllowEdit(true);
            ownersPermission.setAllowEditAll(true);
            ownersPermission.setAllowManageAllUsers(true);
            ownersPermission.setAllowManageUsers(true);
            return ownersPermission;
        }

        List<UserPermission> permissions = em.get()
                                             .createQuery(
                                                     "select u from UserPermission u where u.user = :user and u" +
                                                     ".database = :db",
                                                     UserPermission.class)
                                             .setParameter("user", user)
                                             .setParameter("db", database)
                                             .getResultList();

        if (permissions.isEmpty()) {
            // return a permission with nothing enabled
            return new UserPermission();

        } else {
            return permissions.get(0);
        }
    }

    public static PermissionOracle using(EntityManager em) {
        return new PermissionOracle(Providers.of(em));
    }

}
