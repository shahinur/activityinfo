package org.activityinfo.store.hrd.auth;

/**
 * Created by alex on 10/13/14.
 */
public interface Authorization {
    boolean isOwner();

    boolean canUpdate();

    boolean canView();

    boolean canCreateChildren();

}
