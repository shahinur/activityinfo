package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.store.hrd.auth.Authorizer;
import org.activityinfo.store.hrd.auth.WorkspaceAuthDAO;
import org.activityinfo.store.hrd.auth2.LegacyAuthDAO;
import org.activityinfo.store.hrd.cache.CommitStatusCache;
import org.activityinfo.store.hrd.cache.WorkspaceCache;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.tx.ReadableTx;

public class StoreContext {
    private final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private final WorkspaceCache workspaceCache;
    private final CommitStatusCache commitStatusCache;

    public StoreContext() {
        workspaceCache = new WorkspaceCache();
        commitStatusCache = new CommitStatusCache();
    }


    public WorkspaceCache getWorkspaceCache() {
        return workspaceCache;
    }

    public DatastoreService getDatastore() {
        return datastore;
    }

    public CommitStatusCache getCommitStatusCache() {
        return commitStatusCache;
    }

    public Authorizer createAuthorizer(WorkspaceEntityGroup workspace, AuthenticatedUser user, ReadableTx tx) {
        switch (workspace.getWorkspaceId().getDomain()) {
            case CuidAdapter.DATABASE_DOMAIN:
            case CuidAdapter.COUNTRY_DOMAIN:
                return new LegacyAuthDAO(workspace, user, tx);

            default:
                return new WorkspaceAuthDAO(workspace, user, tx);
        }
    }
}
