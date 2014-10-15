package org.activityinfo.store.hrd.dao;

import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.WritableTx;

/**
 * Listens for changes to a workspace and
 * updates indices/caches as necessary
 */
public interface Interceptor {

    public UpdateInterceptor beginUpdate(WorkspaceEntityGroup workspace, AuthenticatedUser user, WritableTx tx);


}
