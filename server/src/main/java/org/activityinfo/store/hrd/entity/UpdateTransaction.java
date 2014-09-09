package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.*;
import org.activityinfo.model.auth.AuthenticatedUser;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.appengine.api.datastore.TransactionOptions.Builder.withXG;

/**
 * Encapsulates the parameters and state of a transaction on a Workspace.
 */
public class UpdateTransaction implements WorkspaceTransaction {

    private static final Logger LOGGER = Logger.getLogger(UpdateTransaction.class.getName());

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

    public UpdateTransaction(Workspace workspace, DatastoreService datastoreService, AuthenticatedUser user) {
        this.datastoreService = datastoreService;
        this.workspace = workspace;
        this.user = user;

        transaction = datastoreService.beginTransaction(withXG(true));
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    /**
     * Increments the global version number
     * @return the new global version number
     */
    @Override
    public long incrementVersion() {
        if(currentVersion < 0) {
            initialVersion = workspace.getVersion().get(this);
            currentVersion = initialVersion;
        }
        return ++currentVersion;
    }

    @Override
    public Entity get(Key key) throws EntityNotFoundException {
        return datastoreService.get(transaction, key);
    }

    @Override
    public void put(Entity entity) {
        datastoreService.put(transaction, entity);
    }

    @Override
    public void put(Iterable<Entity> entities) {
        for(Entity entity : entities) {
            System.out.println(entity.getKey() + " = " + entity);
        }
        datastoreService.put(transaction, entities);
    }


    @Override
    public PreparedQuery prepare(Query projection) {
        return datastoreService.prepare(transaction, projection);
    }

    /**
     * Commit this transaction.
     */
    @Override
    public void commit() {
        if(currentVersion > initialVersion) {
            workspace.getVersion().put(this, currentVersion);
        }

        transaction.commit();
        committed = true;
    }

    @Override
    public void close() {
        if (!committed) {
            LOGGER.warning("Transaction on workspace " + workspace.getWorkspaceId() + " closed without committing, " +
                "rolling back...");
            try {
                transaction.rollback();
            } catch(Exception e) {
                LOGGER.log(Level.WARNING, "Exception while rolling back uncomitted workspace transaction");
            }
        }
    }

    @Override
    public AuthenticatedUser getUser() {
        return user;
    }

}
