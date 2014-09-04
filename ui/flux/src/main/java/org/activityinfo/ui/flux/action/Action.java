package org.activityinfo.ui.flux.action;

import org.activityinfo.ui.flux.store.Store;

/**
 *
 * @param <H> The Handler type for this action
 */
public interface Action<H> {

    /**
     *
     * If the {@code listener}  object implements this {@code Action}s
     * handler interface {@code H}, this {@code Action} will invoke the
     * callback defined on {@code H}
     *
     */
    void accept(Store store);
}
