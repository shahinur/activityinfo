package org.activityinfo.store.hrd.tx;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * Writable transaction that guards against reads until the completion
 * phase has begun.
 *
 * <p>This just allows us to safely pass a reference to the transaction
 * to interceptors during a two-phase bulk operation and ensure that the transaction
 * is not actually used until we enter the second phase.
 */
public class TwoPhaseWriteTx implements WritableTx {

    private final DatastoreService datastore;

    /**
     * The datastore transaction used during the completion phase
     */
    private Transaction transaction = null;


    public TwoPhaseWriteTx(DatastoreService datastore) {
        this.datastore = datastore;
    }


    @Override
    public void put(IsEntity entity) {
        Preconditions.checkState(transaction != null, "Completion phase has not begun.");
        datastore.put(transaction, entity.toEntity());
    }


    public TwoPhaseWriteTx beginCompletion() {
        Preconditions.checkState(transaction == null, "Completion phase has already begun");
        transaction = datastore.beginTransaction();
        return this;
    }

    @Override
    public void commit() {
        Preconditions.checkState(transaction != null, "Completion phase has not begun");
        transaction.commit();
    }

    @Override
    public void rollback() {
        Preconditions.checkState(transaction != null, "Completion phase has not begun");
        transaction.commit();
    }

    @Override
    public <T extends IsEntity> T getOrThrow(IsKey<T> key) {
        Preconditions.checkState(transaction != null, "Reads are only supported in completion phase");
        return Mapping.getOrThrow(datastore, transaction, key);
    }

    @Override
    public <T extends IsEntity> Optional<T> getIfExists(IsKey<T> key) {
        Preconditions.checkState(transaction != null, "Reads are only supported in completion phase");
        return Mapping.getIfExists(datastore, transaction, key);
    }
}
