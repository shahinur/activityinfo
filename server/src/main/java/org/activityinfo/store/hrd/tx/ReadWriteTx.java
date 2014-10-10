package org.activityinfo.store.hrd.tx;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Optional;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadWriteTx implements WritableTx, AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(ReadWriteTx.class.getName());

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
        LOGGER.info("Committing " + transaction.getId() + "...");
        transaction.commit();
        LOGGER.info("Transaction " + transaction.getId() + "committed.");
    }

    public void rollback() {
        transaction.rollback();
    }

    @Override
    public void close() {
        if (transaction.isActive()) {
            try {
                LOGGER.info("Rolling back uncommitted transaction... " + transaction.getId());
                transaction.rollback();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Exception thrown while rolling back transaction " + transaction.getId(), e);
            }
        }
    }
}
