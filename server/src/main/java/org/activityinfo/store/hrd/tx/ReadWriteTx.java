package org.activityinfo.store.hrd.tx;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Optional;

public class ReadWriteTx implements ReadableTx, AutoCloseable {
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
        System.out.println(datastoreEntity);
        this.datastore.put(transaction, datastoreEntity);
    }

    public <T extends IsEntity> T getOrThrow(IsKey<T> key) {
        return Operations.getOrThrow(datastore, transaction, key);
    }

    public <T extends IsEntity> Optional<T> getIfExists(IsKey<T> key) {
        return Operations.getIfExists(datastore, transaction, key);
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
