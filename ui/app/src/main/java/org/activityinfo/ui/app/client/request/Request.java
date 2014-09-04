package org.activityinfo.ui.app.client.request;

import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.RemoteStoreService;

public interface Request<T> {

    static int NEXT_REQUEST_ID = 1;

    Promise<T> send(RemoteStoreService service);

}
