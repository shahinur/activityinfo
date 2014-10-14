package org.activityinfo.store.hrd.tx;

import com.google.appengine.api.datastore.*;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadTx implements ReadableTx, AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(ReadTx.class.getName());

    private final DatastoreService datastore;
    private final Transaction transaction;


    private ReadTx(DatastoreService datastore, Transaction transaction) {
        this.datastore = datastore;
        this.transaction = transaction;

        if(transaction != null) {
            LOGGER.info("Transaction " + transaction.getId() + " begun.");
        }
    }

    /**
     * Creates a new {@code ReadTx} outside of an AppEngine transaction.
     *
     * <p>Datastore operations outside transactions most closely resemble the Read Committed isolation level.
     * Entities retrieved from the datastore by queries or gets will only see committed data.
     * A retrieved entity will never have partially committed data (some from before a commit and some from after).
     *
     * @see <a href="https://cloud.google.com/appengine/articles/transaction_isolation">Transaction Isolation in App Engine</a>
     * @return a new {@code ReadTx}
     */
    public static ReadTx outsideTransaction() {
        return new ReadTx(DatastoreServiceFactory.getDatastoreService(), null);
    }

    /**
     * Creates a new {@code ReadTx} with serializable isolation.
     *
     * <p>n a transaction, all reads reflect the current, consistent state of the Datastore at the time
     * the transaction started. This does not include previous puts and deletes inside the transaction.
     * Queries and gets inside a transaction are guaranteed to see a single, consistent snapshot of the Datastore
     * as of the beginning of the transaction. Entities and index rows in the transaction's entity group are fully
     * updated so that queries return the complete, correct set of result entities, without the false positives or
     * false negatives described in Transaction Isolation that can occur in queries outside of transactions.
     *
     */
    public static ReadTx withSerializableConsistency() {
        return withSerializableConsistency(DatastoreServiceFactory.getDatastoreService());
    }

    /**
     * Creates a new {@code ReadTx} with serializable isolation.
     *
     * <p>n a transaction, all reads reflect the current, consistent state of the Datastore at the time
     * the transaction started. This does not include previous puts and deletes inside the transaction.
     * Queries and gets inside a transaction are guaranteed to see a single, consistent snapshot of the Datastore
     * as of the beginning of the transaction. Entities and index rows in the transaction's entity group are fully
     * updated so that queries return the complete, correct set of result entities, without the false positives or
     * false negatives described in Transaction Isolation that can occur in queries outside of transactions.
     *
     * @param datastore the datastore service to wrap
     */
    public static ReadTx withSerializableConsistency(DatastoreService datastore) {
        LOGGER.info("Beginning serialized read transaction...");
        Transaction tx = datastore.beginTransaction();
        return new ReadTx(datastore, tx);
    }


    public static ReadTx crossGroup() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction tx = datastore.beginTransaction(TransactionOptions.Builder.withXG(true));
        return new ReadTx(datastore, tx);
    }

    /**
     * Fetches an entity from the datastore or throws {@code IllegalStateException}
     *
     * <p>This method should be used if the entity is expected to be present. The {@code IllegalStateException}
     * thrown in the absence of the entity signals that the datastore does not match the expected state
     * and has likely been affected by a programming error.
     *
     * <p>Use {@link #getIfExists(IsKey)} if the absence of the entity
     * is a legitimate state. For example, when fetching the {@code LatestVersion} entity from the datastore
     * at the request of the user, the result of the operation is well-defined: the server should return 404.
     *
     * @param key Entity key
     * @param <T> The Entity wrapper type to be returned
     * @return the entity wrapped as T
     * @throws java.lang.IllegalStateException if the entity does not exist
     */
    public <T extends IsEntity> T getOrThrow(IsKey<T> key) {
        return Mapping.getOrThrow(datastore, transaction, key);
    }

    /**
     * Tries to fetch an empty from the datastore.
     * @param key the datastore entity key
     * @param <T> the entity wrapper type
     * @return the entity wrapper, or {@code Optional.absent()} if the entity does not exist in the datastore.
     */
    public <T extends IsEntity> Optional<T> getIfExists(IsKey<T> key) {
        return Mapping.getIfExists(datastore, transaction, key);
    }

    public <T extends IsEntity> Iterable<T> getList(List<? extends IsKey<T>> keys) {
        return Mapping.getList(datastore, transaction, keys);
    }

    public <T> Iterable<T> query(final ListQuery<T> query) {
        return query(query, FetchOptions.Builder.withDefaults());
    }

    public <T> Iterable<T> query(final ListQuery<T> query, FetchOptions options) {
        Iterable<Entity> iterable = datastore.prepare(transaction, query.getDatastoreQuery()).asIterable(options);
        return Iterables.transform(iterable, query.getTransformer());
    }

    public <T> Optional<T> query(final SingleResultQuery<T> query) {
        return Mapping.query(datastore, transaction, query);
    }

    @Override
    public void close() {

        if(transaction != null) {
            LOGGER.info("Closing transaction " + transaction.getId() + ": active = " + transaction.isActive());
        }

        if(this.transaction != null && this.transaction.isActive()) {
            try {
                // Both commit and rollback are no-ops for read only txs
                this.transaction.commit();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Exception thrown while closing read transaction " + transaction.getId(), e);
            }
        }
    }

}
