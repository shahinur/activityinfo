package org.activityinfo.store.hrd;

import com.google.common.collect.Maps;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.system.ApplicationClassProvider;
import org.activityinfo.model.table.ColumnSet;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.store.*;
import org.activityinfo.service.tables.StoreAccessor;
import org.activityinfo.service.tables.TableBuilder;
import org.activityinfo.service.tree.FormClassProvider;
import org.activityinfo.service.tree.FormTreeBuilder;
import org.activityinfo.store.hrd.dao.WorkspaceQuery;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.index.WorkspaceIndex;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HrdStoreReader implements StoreReader, FormClassProvider {

    private static final Logger LOGGER = Logger.getLogger(HrdResourceStore.class.getName());

    private final StoreContext context;
    private final AuthenticatedUser user;

    private ApplicationClassProvider classProvider = new ApplicationClassProvider();

    /**
     * Maintain one transaction per workspace
     */
    private final Map<WorkspaceEntityGroup, WorkspaceQuery> transactions = Maps.newHashMap();


    public HrdStoreReader(StoreContext context, AuthenticatedUser user) {
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
    public UserResource getResource(ResourceId resourceId) {
        return getWorkspaceOf(resourceId).getResource(resourceId).asUserResource();
    }

    @Override
    public FormTree getFormTree(ResourceId formClassId) {
        return new FormTreeBuilder(this).queryTree(formClassId);
    }

    @Override
    public ResourceNode getResourceNode(ResourceId resourceId) {
        return getWorkspaceOf(resourceId).getResource(resourceId).asResourceNode();
    }

    @Override
    public Iterable<ResourceNode> getFolderItems(ResourceId parentId) {
        return getWorkspaceOf(parentId).getResource(parentId).getFolderItems();
    }

    @Override
    public List<ResourceNode> getOwnedOrSharedWorkspaces() {
        return WorkspaceIndex.queryUserWorkspaces(user);
    }

    @Override
    public List<ResourceVersion> getSnapshots(ResourceId instanceId) {
        return getWorkspaceOf(instanceId).getResource(instanceId).getSnapshots();
    }

    @Override
    public Map<ResourceId, UserResource> getResources(Set<ResourceId> resourceIds) {
        Map<ResourceId, UserResource> map = new HashMap<>();
        for(ResourceId id : resourceIds) {
            try {
                map.put(id, getResource(id));
            } catch(ResourceNotFound | ResourceDeletedException | UnauthorizedException e) {
                // ignore
            }
        }
        return map;
    }

    @Override
    public ColumnSet queryColumns(TableModel tableModel) {
        TableBuilder builder = new TableBuilder(new HrdAccessor());
        try {
            return builder.buildTable(tableModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) throws Exception {
        WorkspaceQuery workspace = getWorkspaceOf(formClassId);

        Iterator<Resource> iterator = workspace.getResource(formClassId).getFormInstances();

        return new HrdCursor(iterator);
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

    @Override
    public FormClass getFormClass(ResourceId resourceId) {
        if(resourceId.isApplicationDefined()) {
            return classProvider.get(resourceId);
        } else {
            return FormClass.fromResource(getResource(resourceId).getResource());
        }
    }

    private class HrdAccessor implements StoreAccessor {

        @Override
        public Resource get(ResourceId resourceId) {
            return getResource(resourceId).getResource();
        }

        @Override
        public ResourceCursor openCursor(ResourceId formClassId) throws Exception {
            WorkspaceQuery workspace = getWorkspaceOf(formClassId);
            Iterator<Resource> iterator = workspace.getResource(formClassId).getFormInstances();
            return new HrdCursor(iterator);
        }

        @Override
        public FormClass getFormClass(ResourceId resourceId) {
            return HrdStoreReader.this.getFormClass(resourceId);
        }
    }
}
