package org.activityinfo.ui.vdom.client.flux.action;

public interface RetryHandler {

    /**
     * Initiates an attempt to retry loading this store.
     */
    void retry();

}
