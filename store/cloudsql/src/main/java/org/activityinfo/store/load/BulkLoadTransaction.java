package org.activityinfo.store.load;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;

public class BulkLoadTransaction {

    private AuthenticatedUser user;
    private WorkspaceEntityGroup workspace;
    private long temporaryVersionNumber;

    private DatastoreService datastore;

    public BulkLoadTransaction(AuthenticatedUser user, WorkspaceEntityGroup workspace, long temporaryVersionNumber) {
        this.user = user;
        this.workspace = workspace;
        this.temporaryVersionNumber = temporaryVersionNumber;
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public WorkspaceEntityGroup getWorkspace() {
        return workspace;
    }

    public long incrementVersion() {
        return temporaryVersionNumber;
    }

}
