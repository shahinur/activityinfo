package org.activityinfo.ui.vdom.client.flux.action;

import org.activityinfo.promise.Promise;
import org.activityinfo.ui.vdom.client.flux.store.Store;

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
    Promise<Void> accept(Store listener);
}
