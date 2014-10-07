package org.activityinfo.store.hrd.dao;

import org.activityinfo.store.hrd.entity.workspace.LatestVersion;

public abstract class UpdateInterceptor {

    /**
     * Called when a new resource has been created, but before the latestVersion
     * has been written to the datastore.
     * <p/>
     * <p>This updater <em>MAY</em> modify {@code latestVersion}</p>
     */
    public void onResourceCreated(LatestVersion latestVersion) {

    }

    public void onResourceUpdated(LatestVersion latestVersion) {

    }

    /**
     * Called prior to committing the update to the datastore. Interceptors should
     * ensure that any pending updates to indices are written to the datastore.
     * @param updateVersion
     */
    public void flush(long updateVersion) {

    }
}
