package org.activityinfo.ui.app.client.request;

import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.promise.Promise;

/**
 * Pings the server to test the connection
 */
public class Ping implements Request<Void> {

    @Override
    public Promise<Void> send(ActivityInfoAsyncClient service) {
        return service.ping();
    }
}
