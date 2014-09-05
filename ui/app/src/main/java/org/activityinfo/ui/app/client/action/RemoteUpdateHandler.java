package org.activityinfo.ui.app.client.action;

import org.activityinfo.ui.app.client.request.Request;

/**
 * A {@code Store} that accepts remote updates
 */
public interface RemoteUpdateHandler {

    void requestStarted(Request request);

    void requestFailed(Request request, Exception e);

    <R> void processUpdate(Request<R> request, R response);

}
