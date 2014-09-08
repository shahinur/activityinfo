package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.common.collect.Maps;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.store.StoreAccessor;
import org.activityinfo.store.hrd.entity.Workspace;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;
import org.activityinfo.store.hrd.index.WorkspaceLookup;

import java.util.Iterator;
import java.util.Map;

public class HrdStoreAccessor implements StoreAccessor {

    private final DatastoreService datastore;
    private final WorkspaceLookup workspaceLookup;
    private final AuthenticatedUser user;

    /**
     * Maintain one transaction per workspace
     */
    private final Map<ResourceId, WorkspaceTransaction> transactions = Maps.newHashMap();


    public HrdStoreAccessor(DatastoreService datastore, WorkspaceLookup workspaceLookup, AuthenticatedUser user) {
        this.datastore = datastore;
        this.workspaceLookup = workspaceLookup;
        this.user = user;
    }

    private WorkspaceTransaction getTransactionFor(ResourceId formClassId) {
        Workspace workspaceId = workspaceLookup.lookup(formClassId);
        WorkspaceTransaction tx = transactions.get(workspaceId);
        if(tx == null) {
            tx = new WorkspaceTransaction(workspaceId, datastore, user);
            transactions.put(workspaceId.getWorkspaceId(), tx);
        }
        return tx;
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) throws Exception {
        WorkspaceTransaction tx = getTransactionFor(formClassId);
        Workspace workspace = tx.getWorkspace();

        Iterator<Resource> iterator = workspace.getLatestContent(formClassId).queryFormInstances(tx);

        return new HrdCursor(iterator);
    }

    @Override
    public Resource get(ResourceId formClassId) throws Exception {
        WorkspaceTransaction tx = getTransactionFor(formClassId);
        Workspace workspace = tx.getWorkspace();

        return workspace.getLatestContent(formClassId).get(tx);
    }

    @Override
    public void close() {

    }
}
