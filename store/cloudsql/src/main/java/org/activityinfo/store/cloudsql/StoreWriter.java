package org.activityinfo.store.cloudsql;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.activityinfo.model.resource.Resource;

import java.sql.SQLException;

/**
 * We ensure a consistent, serial versioning of our store by serializing
 * writes, in a fashion inspired by Datomic's transactor process.
 */
public class StoreWriter implements AutoCloseable {


    private final StoreConnection connection;
    private final StoreCache cache;


    /**
     * Attempts to acquire the global write lock within a one second time period, o
     * @throws org.activityinfo.store.cloudsql.GlobalLockTimeoutException if the lock could not be acquired within the
     * specified timeout
     */
    public StoreWriter(StoreConnection connection, StoreCache cache) throws SQLException, GlobalLockTimeoutException {
        this.connection = connection;
        this.cache = cache;
        int lockResult = connection.queryInteger("SELECT GET_LOCK('update', 1)").get();
        if(lockResult != 1) {
            throw new GlobalLockTimeoutException();
        }
        connection.setAutoCommit(false);
    }

    /**
     * Releases the global write lock.
     * @throws SQLException
     */
    @Override
    public void close() throws SQLException {
        connection.setAutoCommit(true);
        Optional<Integer> releaseStatusCode = connection.queryInteger("SELECT RELEASE_LOCK('update')");
        Preconditions.checkState(releaseStatusCode.isPresent());
        Preconditions.checkState(releaseStatusCode.get() == 1, "Failed to release lock");
    }

    public ResourceUpdate put(Resource resource) {
        return new ResourceUpdate(connection, cache).put(resource);
    }

}
