package org.activityinfo.ui.app.client.request;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.ui.app.client.action.RemoteUpdate;
import org.activityinfo.ui.app.client.chrome.connectivity.ConnectivityState;
import org.activityinfo.ui.app.client.chrome.connectivity.UpdateConnectivityAction;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The request launcher should be used by views to initiate remote calls
 * and dispatch their results as actions in a uniform way.
 *
 * The Facebook people call this a "ActionCreator"
 */
public class RequestDispatcher {

    private static final Logger LOGGER = Logger.getLogger(Request.class.getName());

    private final Dispatcher dispatcher;
    private final RemoteStoreService service;

    private final Map<Request, Promise<?>> pendingRequests = Maps.newHashMap();

    public RequestDispatcher(Dispatcher dispatcher, RemoteStoreService service) {
        this.dispatcher = dispatcher;
        this.service = service;
    }

    public <T> Promise<T> execute(final Request<T> request) {

        LOGGER.info("Executing " + request.getClass().getSimpleName());

        Promise<T> promise = (Promise<T>) pendingRequests.get(request);
        if(promise == null || promise.isSettled()) {
            promise = request.send(service);
            pendingRequests.put(request, promise);
        }
        promise.then(new AsyncCallback<Object>() {
            @Override
            public void onFailure(Throwable caught) {
                fireFailureAction(request, caught);
            }

            @Override
            public void onSuccess(Object result) {
                fireSuccessAction(request, result);
            }
        });

        return promise;
    }

    private void fireSuccessAction(Request<?> request, Object result) {
        deque(request);
        dispatcher.dispatch(new RemoteUpdate(request, result));
        dispatcher.dispatch(new UpdateConnectivityAction(ConnectivityState.ONLINE));
    }

    private void fireFailureAction(Request<?> request, Throwable caught) {
        try {
            deque(request);
        } finally {

            LOGGER.log(Level.SEVERE, "Request failed:"  + caught.getMessage(), caught);

            if (caught instanceof org.activityinfo.ui.store.remote.client.StatusCodeException) {
                // do nothing, application exception
            } else {
                // everything else is considered as connection problem
                dispatcher.dispatch(new UpdateConnectivityAction(ConnectivityState.OFFLINE));
            }
        }
    }

    private void deque(Request<?> request) {
        if (pendingRequests.get(request).isSettled()) {
            pendingRequests.remove(request);
        }
    }
}
