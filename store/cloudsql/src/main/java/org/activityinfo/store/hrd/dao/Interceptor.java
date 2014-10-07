package org.activityinfo.store.hrd.dao;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadWriteTx;

/**
 * Implements a transverse aspect
 */
public abstract class Interceptor {

    public UpdateInterceptor createUpdateInterceptor(WorkspaceEntityGroup entityGroup, AuthenticatedUser user, ReadWriteTx transaction) {
        return null;
    }

    void onWorkspaceCreated(AuthenticatedUser user, WorkspaceEntityGroup group) {

    }
}
