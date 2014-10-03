package org.activityinfo.ui.app.client.action;

import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.flux.action.Action;
import org.activityinfo.ui.flux.store.Store;

public class RequestFailed implements Action {

    private Request request;
    private Throwable exception;

    public RequestFailed(Request request, Throwable exception) {
        this.request = request;
        this.exception = exception;
    }

    @Override
    public void accept(Store store) {
        if(store instanceof RemoteUpdateHandler) {
            ((RemoteUpdateHandler) store).requestFailed(request, exception);
        }
    }
}
