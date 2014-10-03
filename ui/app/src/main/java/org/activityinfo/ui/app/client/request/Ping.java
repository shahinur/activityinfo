package org.activityinfo.ui.app.client.request;

import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;

/**
 * Pings the server to test the connection
 */
public class Ping implements Request<Void> {

    @Override
    public Promise<Void> send(RemoteStoreService service) {
        return service.ping();
    }
}
