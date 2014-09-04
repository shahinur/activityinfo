package org.activityinfo.ui.app.client.store;

import org.activityinfo.ui.app.client.action.RemoteUpdateHandler;
import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;

/**
 * The ResourceCache manages the caching and retrieval of individual
 * resource objects.
 *
 */
public class ResourceStore extends AbstractStore implements RemoteUpdateHandler {


    public ResourceStore(Dispatcher dispatcher) {
        super(dispatcher);
    }


    @Override
    public void requestStarted(Request request) {

    }

    @Override
    public void requestFailed(Request request, Exception e) {

    }

    @Override
    public <R> void processUpdate(Request<R> request, R response) {

    }
}
