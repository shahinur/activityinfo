package org.activityinfo.ui.app.client.request;

import org.activityinfo.client.ActivityInfoAsyncClient;
import org.activityinfo.promise.Promise;

public interface Request<T> {

    static int NEXT_REQUEST_ID = 1;

    Promise<T> send(ActivityInfoAsyncClient service);

}
