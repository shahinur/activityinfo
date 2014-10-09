package org.activityinfo.store.blob;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.base.Optional;
import org.activityinfo.service.blob.BlobId;

import java.util.logging.Logger;

/**
 * Handles storage of Blob Metadata using the AppEngine datastore
 */
class MetadataTransaction implements AutoCloseable {

    private final Logger LOGGER = Logger.getLogger(MetadataTransaction.class.getName());

    private final DatastoreService datastore;
    private final Transaction tx;

    public MetadataTransaction() {
        LOGGER.info("Beginning metadata tx...");
        datastore = DatastoreServiceFactory.getDatastoreService();
        tx = datastore.beginTransaction();
        LOGGER.info("Transaction " + tx.getId() + " is begun.");

    }

    public Optional<UserBlob> get(BlobId blobId) {
        try {
            return Optional.of(UserBlob.fromEntity(datastore.get(tx, UserBlob.metadataKey(blobId))));
        } catch (EntityNotFoundException e) {
            return Optional.absent();
        }
    }

    public void put(UserBlob userBlob) {
        datastore.put(tx, userBlob.toEntity());
    }


    public static Optional<UserBlob> getUserBlob(BlobId blobId) {
        try (MetadataTransaction tx = new MetadataTransaction()) {
            return tx.get(blobId);
        }
    }

    public void commit() {
        LOGGER.info("Committing " + tx.getId() + "...");
        tx.commit();
        LOGGER.info("Transaction " + tx.getId() + "committed.");

    }

    @Override
    public void close()  {

        if(tx.isActive()) {
            tx.rollback();
        }
    }
}
