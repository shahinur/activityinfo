package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import org.activityinfo.store.hrd.cache.CommitStatusCache;
import org.activityinfo.store.hrd.cache.WorkspaceCache;

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
}
