package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.common.collect.Maps;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.store.StoreAccessor;
import org.activityinfo.store.hrd.dao.WorkspaceQuery;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
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
    private final Map<WorkspaceEntityGroup, WorkspaceQuery> transactions = Maps.newHashMap();


    public HrdStoreAccessor(DatastoreService datastore, WorkspaceLookup workspaceLookup, AuthenticatedUser user) {
        this.datastore = datastore;
        this.workspaceLookup = workspaceLookup;
        this.user = user;
    }

    private WorkspaceQuery getWorkspaceOf(ResourceId formClassId) {
        WorkspaceEntityGroup workspace = workspaceLookup.lookupGroup(formClassId);
        WorkspaceQuery tx = transactions.get(workspace);
        if(tx == null) {
            tx = new WorkspaceQuery(workspace, user);
            transactions.put(workspace, tx);
        }
        return tx;
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) throws Exception {
        WorkspaceQuery workspace = getWorkspaceOf(formClassId);

        Iterator<Resource> iterator = workspace.getResource(formClassId).getFormInstances();

        return new HrdCursor(iterator);
    }

    @Override
    public Resource get(ResourceId formClassId) throws Exception {
        WorkspaceQuery tx = getWorkspaceOf(formClassId);

        return tx.getResource(formClassId).asUserResource().getResource();
    }

    @Override
    public void close() {

    }
}
