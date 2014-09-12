package org.activityinfo.store.hrd.entity;

import com.google.appengine.api.datastore.*;
import org.activityinfo.model.auth.AuthenticatedUser;

import static com.google.appengine.api.datastore.TransactionOptions.Builder.withXG;

public class ReadTransaction implements WorkspaceTransaction {

    private final DatastoreService datastoreService;
    private final Workspace workspace;
    private final AuthenticatedUser user;
    private final Transaction transaction;

    private boolean committed = false;


    public ReadTransaction(Workspace workspace, DatastoreService datastoreService, AuthenticatedUser user) {
        this.datastoreService = datastoreService;
        this.workspace = workspace;
        this.user = user;

        transaction = datastoreService.beginTransaction(withXG(true));
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public long incrementVersion() {
        throw new UnsupportedOperationException("Read-only transaction in progress.");
    }

    @Override
    public Entity get(Key key) throws EntityNotFoundException {
        return datastoreService.get(key);
    }

    @Override
    public void put(Entity entity) {
        throw new UnsupportedOperationException("Read-only transaction in progress.");
    }

    @Override
    public void put(Iterable<Entity> entities) {
        throw new UnsupportedOperationException("Read-only transaction in progress.");
    }

    @Override
    public PreparedQuery prepare(Query query) {
        return datastoreService.prepare(query);
    }

    @Override
    public void commit() {
        transaction.commit();
        committed = true;
    }

    @Override
    public void close() {
        if(!committed) {
            transaction.commit();
        }
    }

    @Override
    public AuthenticatedUser getUser() {
        return user;
    }
}
