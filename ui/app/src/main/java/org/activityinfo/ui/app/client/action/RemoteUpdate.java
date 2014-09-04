package org.activityinfo.ui.app.client.action;

import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.flux.action.Action;
import org.activityinfo.ui.flux.store.Store;

/**
 * An action that includes updates from the
 * remote server and instructs stores to update their local state
 */
public class RemoteUpdate implements Action {

    private final Request request;
    private final Object response;

    public RemoteUpdate(Request request, Object response) {
        this.request = request;
        this.response = response;
    }

    @Override
    public void accept(Store store) {
        if(store instanceof RemoteUpdateHandler) {
            ((RemoteUpdateHandler) store).processUpdate(request, response);
        }
    }
}
