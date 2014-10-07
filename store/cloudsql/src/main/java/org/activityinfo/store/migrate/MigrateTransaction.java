package org.activityinfo.store.migrate;

/**
 * We can't fit a whole migration inside a transaction, so we merely wait until
 * all the inserts are complete before adding the workspace to the user's index.
 */
public class MigrateTransaction  {

//    /**
//     * AppEngine will take care of sending out puts in batches,
//     * but it's better to do the serialization to protobuf in batches
//     */
//    private static final int BATCH_LIMIT = 500;
//
//    private DatastoreService datastore;
//    private Workspace workspace;
//    private AuthenticatedUser user;
//    private long currentVersion = 0;
//
//    private List<Entity> toWrite = Lists.newArrayList();
//    private List<Key> toDelete = Lists.newArrayList();
//
//    private long timestamp = System.currentTimeMillis();
//
//    public MigrateTransaction(DatastoreService datastore, Workspace workspace, AuthenticatedUser user) {
//        this.datastore = datastore;
//        this.workspace = workspace;
//        this.user = user;
//    }
//
//    @Override
//    public Workspace getWorkspace() {
//        return workspace;
//    }
//
//    @Override
//    public long incrementVersion() {
//        return ++currentVersion;
//    }
//
//    @Override
//    public Entity get(Key key) throws EntityNotFoundException {
//        return datastore.get(null, key);
//    }
//
//    @Override
//    public void put(Entity entity) {
//        toWrite.add(entity);
//        maybeSendBatch();
//    }
//
//    @Override
//    public void put(Iterable<Entity> entities) {
//        Iterables.addAll(toWrite, entities);
//        maybeSendBatch();
//    }
//
//    @Override
//    public void delete(Key key) {
//        toDelete.add(key);
//        maybeSendBatch();
//    }
//
//    @Override
//    public void delete(Iterable<Key> entities) {
//        Iterables.addAll(toDelete, entities);
//        maybeSendBatch();
//    }
//
//    private void maybeSendBatch() {
//        int size = toWrite.size() + toDelete.size();
//        if(size > BATCH_LIMIT) {
//            flush();
//        }
//    }
//
//    @Override
//    public PreparedQuery prepare(Query projection) {
//        return datastore.prepare(null, projection);
//    }
//
//    @Override
//    public void commit() {
//        flush();
//    }
//
//    @Override
//    public void close() {
//        flush();
//    }
//
//    @Override
//    public AuthenticatedUser getUser() {
//        return user;
//    }
//
//    public void setTimestamp(long timestamp) {
//        this.timestamp = timestamp;
//    }
//
//    @Override
//    public long currentTimeMillis() {
//        return timestamp;
//    }
//
//    public void flush() {
//        datastore.put(null, toWrite);
//        datastore.delete(toDelete);
//
//        toWrite.clear();
//        toDelete.clear();
//    }
}
