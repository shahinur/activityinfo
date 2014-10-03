package org.activityinfo.ui.app.client.action;

import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.flux.action.Action;
import org.activityinfo.ui.flux.store.Store;

public class RequestStarted implements Action {

    private final Request request;

    public RequestStarted(Request request) {
        this.request = request;
    }

    @Override
    public void accept(Store store) {
        if(store instanceof RemoteUpdateHandler) {
            ((RemoteUpdateHandler) store).requestStarted(request);
        }
    }
}
