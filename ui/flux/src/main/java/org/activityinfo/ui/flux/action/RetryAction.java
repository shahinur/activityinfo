package org.activityinfo.ui.flux.action;

import org.activityinfo.promise.Promise;
import org.activityinfo.ui.flux.store.RemoteStore;
import org.activityinfo.ui.flux.store.Store;

public class RetryAction implements Action {

    private RemoteStore store;

    /**
     *
     * @param store to retry loading
     */
    public RetryAction(RemoteStore store) {
        this.store = store;
    }

    @Override
    public Promise<Void> accept(Store listener) {

        return Promise.done();
    }
}
