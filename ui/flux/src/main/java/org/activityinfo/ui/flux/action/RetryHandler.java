package org.activityinfo.ui.flux.action;

public interface RetryHandler {

    /**
     * Initiates an attempt to retry loading this store.
     */
    void retry();

}
