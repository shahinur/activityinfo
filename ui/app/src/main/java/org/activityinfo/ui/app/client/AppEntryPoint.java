package org.activityinfo.ui.app.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import org.activityinfo.ui.app.client.store.AppStores;
import org.activityinfo.ui.store.remote.client.RemoteStoreServiceImpl;
import org.activityinfo.ui.store.remote.client.RestEndpoint;

public class AppEntryPoint implements EntryPoint {

    @Override
    public void onModuleLoad() {

        RemoteStoreServiceImpl service = new RemoteStoreServiceImpl(
                new RestEndpoint("/service/store"));

        AppStores app = new AppStores(service);
        RootWidget rootWidget = new RootWidget(app);

        app.getWorkspaceStore().load();

        initializeRoute(app);
    }

    private void initializeRoute(final AppStores app) {
        app.getRouter().updatePath(History.getToken());

        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                app.getRouter().updatePath(event.getValue());
            }
        });
    }
}
