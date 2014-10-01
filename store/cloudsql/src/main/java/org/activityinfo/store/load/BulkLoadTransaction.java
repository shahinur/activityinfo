package org.activityinfo.store.load;

import com.google.appengine.api.datastore.*;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.store.hrd.entity.FormMetadata;
import org.activityinfo.store.hrd.entity.Workspace;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;

public class BulkLoadTransaction implements WorkspaceTransaction {

    private AuthenticatedUser user;
    private Workspace workspace;
    private long temporaryVersionNumber;

    private DatastoreService datastore;

    public BulkLoadTransaction(AuthenticatedUser user, Workspace workspace, long temporaryVersionNumber) {
        this.user = user;
        this.workspace = workspace;
        this.temporaryVersionNumber = temporaryVersionNumber;
        this.datastore = DatastoreServiceFactory.getDatastoreService();
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public long incrementVersion() {
        return temporaryVersionNumber;
    }

    @Override
    public Entity get(Key key) throws EntityNotFoundException {
        if(key.getKind().equals(FormMetadata.KIND)) {

        }
        throw new UnsupportedOperationException("Reads not supported within a BulkLoadTransaction.");
    }

    @Override
    public void put(Entity entity) {
        datastore.put(null, entity);
    }

    @Override
    public void put(Iterable<Entity> entities) {
        datastore.put(null, entities);
    }

    @Override
    public void delete(Key key) {
        datastore.delete(key);
    }

    @Override
    public void delete(Iterable<Key> entities) {
        datastore.delete(entities);
    }

    @Override
    public PreparedQuery prepare(Query projection) {
        throw new UnsupportedOperationException("Reads not supported within a BulkLoadTransaction.");
    }

    @Override
    public void commit() {

    }

    @Override
    public void close() {

    }

    @Override
    public AuthenticatedUser getUser() {
        return user;
    }
}
