package org.activityinfo.ui.app.client;

import org.activityinfo.ui.app.client.page.create.MockRemoteStoreService;

public class ApplicationStub {

    private static MockRemoteStoreService service;

    private static Application application;

    public static Application get() {
        if(application == null) {
            service = new MockRemoteStoreService();
            application = new Application(service);
        }
        return application;
    }

    public static MockRemoteStoreService getService() {
        return service;
    }
}
