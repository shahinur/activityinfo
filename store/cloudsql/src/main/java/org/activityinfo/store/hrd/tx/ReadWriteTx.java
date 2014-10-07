package org.activityinfo.store.hrd.tx;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Optional;

public class ReadWriteTx implements WritableTx, AutoCloseable {
    private final DatastoreService datastore;
    private final Transaction transaction;

    private ReadWriteTx(DatastoreService datastore, Transaction transaction) {
        this.datastore = datastore;
        this.transaction = transaction;
    }

    public static ReadWriteTx serializedCrossGroup() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction tx = datastore.beginTransaction(TransactionOptions.Builder.withXG(true));
        return new ReadWriteTx(datastore, tx);
    }

    public void put(IsEntity entity) {
        Entity datastoreEntity = entity.toEntity();
        this.datastore.put(transaction, datastoreEntity);
    }

    public <T extends IsEntity> T getOrThrow(IsKey<T> key) {
        return Mapping.getOrThrow(datastore, transaction, key);
    }

    public <T extends IsEntity> Optional<T> getIfExists(IsKey<T> key) {
        return Mapping.getIfExists(datastore, transaction, key);
    }

    public void commit() {
        transaction.commit();
    }

    public void rollback() {
        transaction.rollback();
    }

    @Override
    public void close() {
        if(transaction.isActive()) {
            transaction.rollback();
        }
    }
}
