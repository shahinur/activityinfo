package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.search.checkers.Preconditions;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.Resources;

import java.util.List;

import static com.google.appengine.api.datastore.TransactionOptions.Builder.withXG;

/**
 * Encapsulates the parameters and state of a transaction on a Workspace.
 */
public class WorkspaceTransaction implements AutoCloseable {


    private final Workspace workspace;
    private final DatastoreService datastoreService;
    private final Transaction transaction;
    private final AuthenticatedUser user;

    /**
     * The version of the Workspace at the beginning of the transaction,
     * or -1 if the version of the workspace has not yet been fetched
     */
    private long initialVersion = -1;

    /**
     * The current version of the Workspace, or -1 if the current version
     * has not yet been incremented
     */
    private long currentVersion = -1;


    private boolean committed;

    public WorkspaceTransaction(Workspace workspace, DatastoreService datastoreService, AuthenticatedUser user) {
        this.datastoreService = datastoreService;
        this.workspace = workspace;
        this.user = user;

        transaction = datastoreService.beginTransaction(withXG(true));
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    /**
     * Increments the global version number
     * @return the new global version number
     */
    public long incrementVersion() {
        if(currentVersion < 0) {
            initialVersion = workspace.getVersion().get(this);
            currentVersion = initialVersion;
        }
        return ++currentVersion;
    }

    public Entity get(Key key) throws EntityNotFoundException {
        return datastoreService.get(transaction, key);
    }

    public Key put(Entity entity) {
        return datastoreService.put(transaction, entity);
    }

    public List<Key> put(Iterable<Entity> entities) {
        for(Entity entity : entities) {
            System.out.println(entity.getKey() + " = " + entity);
        }
        return datastoreService.put(transaction, entities);
    }


    public PreparedQuery prepare(Query projection) {
        return datastoreService.prepare(transaction, projection);
    }

    /**
     * Commit this transaction.
     */
    public void commit() {
        if(currentVersion > initialVersion) {
            workspace.getVersion().put(this, currentVersion);
        }

        transaction.commit();
        committed = true;
    }

    @Override
    public void close() {
        if (!committed) transaction.rollback();
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    public void createWorkspace(Resource resource) {
        Preconditions.checkArgument(resource.getOwnerId().equals(Resources.ROOT_ID),
            "workspace owner must be ROOT_ID");
        workspace.createResource(this, resource);
    }
}
