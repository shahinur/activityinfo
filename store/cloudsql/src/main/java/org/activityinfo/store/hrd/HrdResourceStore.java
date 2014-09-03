package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.repackaged.com.google.common.collect.Maps;
import com.google.apphosting.api.ApiProxy;
import com.google.common.collect.Lists;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.model.resource.ResourceTree;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.ResourceTreeRequest;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tables.TableBuilder;
import org.activityinfo.store.hrd.entity.GlobalVersion;
import org.activityinfo.store.hrd.entity.ResourceGroup;
import org.activityinfo.store.hrd.entity.Snapshot;
import org.activityinfo.store.hrd.index.AcrIndex;
import org.activityinfo.store.hrd.index.FolderIndex;
import org.activityinfo.store.hrd.index.WorkspaceIndex;

import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Map;

import static com.google.appengine.api.datastore.TransactionOptions.Builder.withXG;

public class HrdResourceStore implements ResourceStore {
    private final static long TIME_LIMIT_MILLISECONDS = 10 * 1000L;
    private final DatastoreService datastore;

    public HrdResourceStore() {
        this(DatastoreServiceFactory.getDatastoreService());
    }

    public HrdResourceStore(DatastoreService datastore) {
        this.datastore = datastore;
    }

    @Override
    public Resource get(@InjectParam AuthenticatedUser user, @PathParam("id") ResourceId resourceId) {
        try {
            ResourceGroup group = new ResourceGroup(resourceId);
            return group.getLatestContent(resourceId).get(datastore);

        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound(resourceId);
        }
    }

    @Override
    public List<Resource> getAccessControlRules(@InjectParam AuthenticatedUser user,
                                                @PathParam("id") ResourceId resourceId) {

         return Lists.newArrayList(AcrIndex.queryRules(datastore, resourceId));
    }

    @Override
    public UpdateResult put(@InjectParam AuthenticatedUser user,
                            @PathParam("id") ResourceId resourceId,
                            Resource resource) {

       return put(user, resource);
    }


    @Override
    public UpdateResult put(AuthenticatedUser user, Resource resource) {

        Transaction tx = datastore.beginTransaction(withXG(true));
        ResourceGroup group = new ResourceGroup(resource.getId());

        try {
            Resource previousVersion = group.getLatestContent(resource.getId()).get(datastore, tx);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound(resource.getId());
        }

        long newVersion = GlobalVersion.incrementVersion(datastore, tx);
        Resource updatedResource = resource.copy();
        updatedResource.setVersion(newVersion);

        group.update(datastore, tx, user, updatedResource);

        tx.commit();

        return UpdateResult.committed(resource.getId(), newVersion);
    }

    @Override
    public UpdateResult create(AuthenticatedUser user, Resource resource) {

        Transaction tx = datastore.beginTransaction(withXG(true));
        ResourceGroup group = new ResourceGroup(resource.getId());

        long newVersion = GlobalVersion.incrementVersion(datastore, tx);
        Resource newResource = resource.copy();
        newResource.setVersion(newVersion);

        group.update(datastore, tx, user, newResource);

        // if this is a root workspace, grant ownership to user
        if(resource.getOwnerId().equals(Resources.ROOT_ID)) {

            AccessControlRule acr = new AccessControlRule(resource.getId(), user.getUserResourceId());
            acr.setOwner(true);
            Resource acrResource = acr.asResource();
            acrResource.setVersion(newVersion);
            group.update(datastore, tx, user, acrResource);

            // add to the index
            datastore.put(tx, WorkspaceIndex.createOwnerIndex(resource.getId(), user));
        }

        tx.commit();

        return UpdateResult.committed(resource.getId(), newVersion);
    }

    @Override
    public ResourceTree queryTree(@InjectParam AuthenticatedUser user, ResourceTreeRequest request) {
        try {
            return new ResourceTree(FolderIndex.queryNode(datastore, request.getRootId()));
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFound(request.getRootId());
        }
    }

    @Override
    public TableData queryTable(@InjectParam AuthenticatedUser user, TableModel tableModel) {
        TableBuilder builder = new TableBuilder(new HrdStoreAccessor(datastore));
        try {
            return builder.buildTable(tableModel);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ResourceNode> getOwnedOrSharedWorkspaces(@InjectParam AuthenticatedUser user) {
        return WorkspaceIndex.queryUserWorkspaces(datastore, user);
    }

    // TODO Authorization must be added, the requested ResourceGroup must be respected, etc.
    public List<Resource> getUpdates(@InjectParam AuthenticatedUser user, ResourceGroup resourceGroup, long version) {
        ApiProxy.Environment environment = ApiProxy.getCurrentEnvironment();
        Map<Key, Snapshot> snapshots = Maps.newLinkedHashMap();

        for (Snapshot snapshot : Snapshot.getSnapshotsAfter(datastore, version)) {
            Key parentKey = snapshot.getParentKey();

            // We want the linked list to be sorted based on the most recent insertion of a resource
            snapshots.remove(parentKey);
            snapshots.put(parentKey, snapshot);

            if (environment.getRemainingMillis() < TIME_LIMIT_MILLISECONDS) break;
        }

        try {
            List<Resource> resources = Lists.newArrayListWithCapacity(snapshots.size());

            for (Snapshot snapshot : snapshots.values()) {
                resources.add(snapshot.get(datastore));
            }

            return resources;
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
