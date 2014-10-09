package org.activityinfo.store.hrd;

import com.google.common.collect.Maps;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.store.StoreAccessor;
import org.activityinfo.store.hrd.dao.WorkspaceQuery;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HrdStoreAccessor implements StoreAccessor {

    private static final Logger LOGGER = Logger.getLogger(HrdResourceStore.class.getName());

    private final StoreContext context;
    private final AuthenticatedUser user;

    /**
     * Maintain one transaction per workspace
     */
    private final Map<WorkspaceEntityGroup, WorkspaceQuery> transactions = Maps.newHashMap();


    public HrdStoreAccessor(StoreContext context, AuthenticatedUser user) {
        this.context = context;
        this.user = user;
    }

    private WorkspaceQuery getWorkspaceOf(ResourceId formClassId) {
        WorkspaceEntityGroup workspace = context.getWorkspaceCache().lookup(formClassId);
        WorkspaceQuery tx = transactions.get(workspace);
        if(tx == null) {
            tx = new WorkspaceQuery(context, workspace, user);
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
        for(WorkspaceQuery query : transactions.values()) {
            try {
                query.close();
            } catch(Exception e) {
                LOGGER.log(Level.SEVERE, "Exception thrown while closing Workspace Query", e);
            }
        }
    }
}
