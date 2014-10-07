package org.activityinfo.store.hrd.dao;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.common.collect.Lists;
import org.activityinfo.store.hrd.entity.workspace.LatestVersion;

import java.util.List;

/**
 * Used by the {@link org.activityinfo.store.hrd.dao.BulkLoader} to batch writes to the datastore
 */
class BatchWriter {

    private int batchSize = 100;

    private final DatastoreService datastore;

    private List<Entity> toWrite = Lists.newArrayList();

    BatchWriter(DatastoreService datastore) {
        this.datastore = datastore;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void put(LatestVersion entity) {
        toWrite.add(entity.toEntity());

        if(toWrite.size() > batchSize) {
            flushBatch();
        }
    }

    public void flushBatch() {
        // Write outside of a transaction to avoid blocking other writes
        // If the transaction fails, the LatestVersions entities may be partially
        // written, but they will not be visible because the final CommitStatus entity
        // will not be written.
        datastore.put(null, toWrite);
        toWrite.clear();
    }
}
