package org.activityinfo.store.migrate;

import com.google.appengine.api.datastore.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.store.hrd.entity.Workspace;
import org.activityinfo.store.hrd.entity.WorkspaceTransaction;

import java.util.List;

/**
 * We can't fit a whole migration inside a transaction, so we merely wait until
 * all the inserts are complete before adding the workspace to the user's index.
 */
public class MigrateTransaction implements WorkspaceTransaction {

    /**
     * AppEngine will take care of sending out puts in batches,
     * but it's better to do the serialization to protobuf in batches
     */
    private static final int BATCH_LIMIT = 500;

    private DatastoreService datastore;
    private Workspace workspace;
    private AuthenticatedUser user;
    private long currentVersion = 0;

    private List<Entity> toWrite = Lists.newArrayList();

    public MigrateTransaction(DatastoreService datastore, Workspace workspace, AuthenticatedUser user) {
        this.datastore = datastore;
        this.workspace = workspace;
        this.user = user;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public long incrementVersion() {
        return ++currentVersion;
    }

    @Override
    public Entity get(Key key) throws EntityNotFoundException {
        return datastore.get(null, key);
    }

    @Override
    public void put(Entity entity) {
        toWrite.add(entity);
        maybeSendBatch();
    }

    @Override
    public void put(Iterable<Entity> entities) {
        Iterables.addAll(toWrite, entities);
        maybeSendBatch();
    }

    private void maybeSendBatch() {
        if(toWrite.size() > BATCH_LIMIT) {
            flush();
        }
    }

    @Override
    public PreparedQuery prepare(Query projection) {
        return datastore.prepare(null, projection);
    }

    @Override
    public void commit() {
        flush();
    }

    @Override
    public void close() {
        flush();
    }

    @Override
    public AuthenticatedUser getUser() {
        return user;
    }

    public void flush() {
        datastore.put(null, toWrite);
        toWrite.clear();
    }
}
