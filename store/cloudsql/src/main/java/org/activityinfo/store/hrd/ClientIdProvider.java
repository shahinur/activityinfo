package org.activityinfo.store.hrd;

import com.google.appengine.api.datastore.*;

import java.util.concurrent.ThreadLocalRandom;

public class ClientIdProvider {

    public static final String KIND = "ClientId";

    public static final String NEXT_ID_PROPERTY = "n";
    public static final int NUM_SHARDS = 62;

    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public long getNext() {

        // use sharded entity groups to avoid contention on the id generation process
        long shard = ThreadLocalRandom.current().nextInt(1, NUM_SHARDS);

        // atomically update the next id within this shard.
        Transaction tx = datastore.beginTransaction();
        long nextId = incrementNextId(shard);
        updateNextId(shard, tx, nextId + 1);
        tx.commit();

        // return to the client
        return (shard * NUM_SHARDS) + nextId;

    }

    private void updateNextId(long shard, Transaction tx, long nextId) {
        Entity entity = new Entity(shardKey(shard));
        entity.setUnindexedProperty(NEXT_ID_PROPERTY, nextId);
        datastore.put(tx, entity);
    }

    private long incrementNextId(long shard) {
        Key key = shardKey(shard);
        Transaction tx = datastore.beginTransaction();
        Entity entity;
        long nextVersion;
        try {
            // existing shard
            entity = datastore.get(tx, key);
            nextVersion = (Long) entity.getProperty(NEXT_ID_PROPERTY);

        } catch (EntityNotFoundException e) {
            // new shard
            entity = new Entity(key);
            nextVersion = 0;
        }
        entity.setUnindexedProperty(NEXT_ID_PROPERTY, nextVersion+1);
        datastore.put(tx, entity);
        return nextVersion;
    }

    private Key shardKey(long shard) {
        return KeyFactory.createKey(KIND, shard);
    }
}
