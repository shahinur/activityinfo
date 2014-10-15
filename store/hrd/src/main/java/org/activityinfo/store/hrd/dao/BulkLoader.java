package org.activityinfo.store.hrd.dao;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.store.StoreLoader;
import org.activityinfo.store.hrd.StoreContext;
import org.activityinfo.store.hrd.cache.WorkspaceCache;
import org.activityinfo.store.hrd.entity.workspace.CommitStatus;
import org.activityinfo.store.hrd.entity.workspace.CommitStatusKey;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;
import org.activityinfo.store.hrd.entity.workspace.WorkspaceEntityGroup;
import org.activityinfo.store.hrd.index.FolderIndex;
import org.activityinfo.store.hrd.index.FormInstanceIndexer;
import org.activityinfo.store.hrd.tx.TwoPhaseWriteTx;

import java.util.List;
import java.util.Set;

/**
 * A transaction which creates a large number of new resources within a workspace without
 * blocking other updates or reads.
 *
 * <p>The BulkLoadTransaction uses a two-phase commit together with a number of constraints to ensure
 * serialized consistency of the bulk transaction with respect to the rest of the workspace.
 *
 * <p>Consider a workspace that looks like the following at version 100:</p>
 *
 * <ul>
 *     <li>Workspace W1 [Version 1]
 *          <ul>
 *              <li>Folder F1 [Version 2]
 *                  <ul>
 *                      <li>Form A [to load]
 *                          <ul><li>10000 resources [to load]</li></ul></li>
*                       <li>Form B [to load]
 *                          <ul><li>20000 resources [to load]</li></ul></li>
 *                  </ul>
 *              </li>
 *          </ul>
 *     </li>
 * </ul>
 *
 * <p>One of the constraints we impose on BulkLoadTransactions is that all the resources to create must be
 * descendants of a single resource.
 *
 * <p>In the first phase, we only write resources to the datastore that are <em>not</em> direct children of the
 * transaction's parent. So in the case above, we defer the creation of Form A and Form B, but write their children
 * directly to the datastore, outside of a datastore transaction.
 *
 * <p>These children are written using {@code createUncommittedResource()}
 * and do not have the normal {@code VERSION_PROPERTY}. Instead, they have a {@code TRANSACTION_ID_PROPERTY} which
 * can be used later to look up their version.
 *
 * <p>Once we have completed all writes of the second-level resources any children they might have, we begin the
 * second phase commit, this time within a normal {@link org.activityinfo.store.hrd.dao.WorkspaceUpdate}.
 * transaction. The top level Form A and Form B are committed with the new version number, and committed together
 * with a {@code TransactionLog} entity which links the transaction id found on the "pending" {@code LatestContent}
 * with their final version number.
 *
 * <p>Note that we don't store {@code Snapshot} entries for the pending entities, one will be created if the
 * resource is ever updated, at which point it will be saved as a "normal" {@code LatestContent} entity.</p>
 *
 * For this to work, there are a number of constraints:
 * <ul>
 *     <li>Only new resources may be created, updates to existing entities are not supported</li>
 *     <li>All new resources must be nested within a small number of parents. The parents are not committed until
 *     the final phase, ensuring that resources created during the first phase are not visible</li>
 *     <li>The top level parents must be a children of a single resource. </li>
 * </ul>
 *
 *
 */
public class BulkLoader implements StoreLoader {
    private StoreContext context;
    private AuthenticatedUser user;
    private WorkspaceEntityGroup workspace;
    private ResourceId parentId;
    private long transactionId;

    private FormInstanceIndexer formIndexer;

    /**
     * Resources that are direct children of this transaction's parent resource.
     * These resources will not be written until the final phase of the commit.
     */
    private List<Resource> pendingResources = Lists.newArrayList();

    /**
     * The resourceIds of those parents who
     */
    private Set<ResourceId> parents = Sets.newHashSet();

    private BatchWriter batchWriter;
    private TwoPhaseWriteTx transaction;

    private BulkLoader(StoreContext context, AuthenticatedUser user, WorkspaceEntityGroup workspace,
                       ResourceId parentId, long transactionId, int batchSize) {
        this.context = context;
        this.user = user;
        this.workspace = workspace;
        this.parentId = parentId;
        this.transactionId = transactionId;
        this.transaction = new TwoPhaseWriteTx(context.getDatastore());
        this.batchWriter = new BatchWriter(context.getDatastore());
        this.batchWriter.setBatchSize(batchSize);
        this.formIndexer = new FormInstanceIndexer(workspace, transaction);
    }

    private void assertAuthorized() {
        try(WorkspaceQuery query = new WorkspaceQuery(context, workspace, user)) {
            query.getResource(parentId).assertCanCreateChildren();
        }
    }

    @Override
    public void create(Resource resource, boolean hasChildren) {
        if(resource.getOwnerId().equals(parentId)) {
            pendingResources.add(resource.copy());
            parents.add(resource.getId());
            formIndexer.onResourceCreated(resource);

        } else if(parents.contains(resource.getOwnerId())) {
            writeUncommittedResource(resource);
            formIndexer.onResourceCreated(resource);
            if(hasChildren) {
                parents.add(resource.getId());
            }
        } else {
            throw new IllegalArgumentException("Only writes to descendants of parent " + parentId + " are allowed.");
        }
    }

    private void writeUncommittedResource(Resource resource) {
        LatestVersion latestVersion = new LatestVersion(workspace, resource);
        latestVersion.setTransactionId(transactionId);
        latestVersion.setRowIndex(formIndexer.nextInstanceIndex(resource));
        latestVersion.setLabel(FolderIndex.formItemLabel(resource));
        batchWriter.put(latestVersion);
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    @Override
    public long commit() {
        batchWriter.flushBatch();

        transaction.beginCompletion();

        try {
            // Begin a "normal" update and update the final phase

            WorkspaceUpdate update = WorkspaceUpdate.newBuilder(context, workspace, user)
                .setTransaction(transaction)
                .begin();

            for (Resource resource : pendingResources) {
                update.createResource(resource);
            }
            update.flush();

            CommitStatus commitStatus = new CommitStatus(workspace, transactionId);
            commitStatus.setCommitVersion(update.getUpdateVersion());
            commitStatus.setUserId(user.getId());
            commitStatus.setCommitTime(System.currentTimeMillis());
            transaction.put(commitStatus);

            transaction.commit();

            for (Resource resource : pendingResources) {
                // Pre-cache these parents as they are likely to be used
                // in queries byt
                context.getWorkspaceCache().cache(resource.getId(), workspace.getWorkspaceId());
            }
            return update.getUpdateVersion();

        } catch(Exception e) {
            transaction.rollback();
            throw e;
        }
    }


    public static Builder newBuilder(StoreContext context, AuthenticatedUser user, ResourceId parentId) {
        return new Builder(context).setUser(user).setParentId(parentId);
    }

    public static Builder newBuilder(StoreContext context) {
        return new Builder(context);
    }


    public static class Builder {
        private DatastoreService datastore;
        private StoreContext context;
        private AuthenticatedUser user;
        private WorkspaceEntityGroup workspace;
        private ResourceId parentId;
        private long transactionId;
        private int batchSize = 100;

        private Builder(StoreContext context) {
            this.context = context;
        }

        public Builder setUser(AuthenticatedUser user) {
            this.user = user;
            return this;
        }

        public Builder setWorkspace(WorkspaceEntityGroup workspace) {
            this.workspace = workspace;
            return this;
        }

        public Builder setParentId(ResourceId parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder setTransactionId(long transactionId) {
            this.transactionId = transactionId;
            return this;
        }


        public Builder setBatchSize(int numResources) {
            batchSize = numResources;
            return this;
        }

        public BulkLoader begin() {

            Preconditions.checkState(user != null, "userId is not set");
            Preconditions.checkState(parentId != null, "parentId is not set");


            if(datastore == null) {
                datastore = DatastoreServiceFactory.getDatastoreService();
            }

            if(workspace == null) {
                WorkspaceCache lookup = new WorkspaceCache();
                workspace = lookup.lookup(parentId);
            }

            if(transactionId == 0) {
                allocateTransactionId();
            }

            BulkLoader loader = new BulkLoader(context, user, workspace, parentId, transactionId, batchSize);

            // check once before we start, we will check again before committing
            loader.assertAuthorized();

            return loader;
        }

        /**
         * Allocate a transaction id. The id is globally unique within the datastore,
         * not yet just the workspace.
         */
        public void allocateTransactionId() {
            Key start = datastore.allocateIds(CommitStatusKey.KIND, 1).getStart();
            transactionId = start.getId();
        }
    }
}
