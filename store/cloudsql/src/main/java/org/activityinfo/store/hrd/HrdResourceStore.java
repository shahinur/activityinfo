package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.collect.Lists;
import com.sun.jersey.api.core.InjectParam;
import org.activityinfo.model.auth.AccessControlRule;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.*;
import org.activityinfo.model.table.TableData;
import org.activityinfo.model.table.TableModel;
import org.activityinfo.service.store.FolderRequest;
import org.activityinfo.service.store.ResourceNotFound;
import org.activityinfo.service.store.ResourceStore;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.service.tables.TableBuilder;
import org.activityinfo.store.cloudsql.BadRequestException;
import org.activityinfo.store.hrd.entity.GlobalVersion;
import org.activityinfo.store.hrd.entity.ResourceGroup;
import org.activityinfo.store.hrd.index.AcrIndex;
import org.activityinfo.store.hrd.index.FolderIndex;
import org.activityinfo.store.hrd.index.WorkspaceIndex;

import javax.ws.rs.PathParam;
import java.util.List;
import java.util.logging.Logger;

import static com.google.appengine.api.datastore.TransactionOptions.Builder.withXG;

public class HrdResourceStore implements ResourceStore {

    private static final Logger LOGGER = Logger.getLogger(HrdResourceStore.class.getName());

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

        // Verify that the resource's owner is valid
        validateOwner(resource);

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

        LOGGER.info("Committed new resource by " + user + ": " + resource);

        return UpdateResult.committed(resource.getId(), newVersion);
    }

    private void validateOwner(Resource resource) {

        ResourceId ownerId = resource.getOwnerId();

        // All users are allowed to create a new workspace of their own
        if(ownerId.equals(Resources.ROOT_ID)) {
            return;
        }

        // Make sure the resource actually exists.
        if(!new ResourceGroup(ownerId).exists(datastore, ownerId)) {

            throw new BadRequestException(String.format(
                "Cannot create resource %s with non-existent owner %s.",
                        resource.getId(), ownerId));
        }
    }

    @Override
    public FolderProjection queryTree(@InjectParam AuthenticatedUser user, FolderRequest request) {
        try {
            return new FolderProjection(FolderIndex.queryNode(datastore, request.getRootId()));
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
}
