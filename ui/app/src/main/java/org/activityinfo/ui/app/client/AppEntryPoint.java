package org.activityinfo.ui.app.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import org.activityinfo.ui.app.client.chrome.Chrome;
import org.activityinfo.ui.app.client.effects.Effects;
import org.activityinfo.ui.app.client.store.Application;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.store.remote.client.RemoteStoreServiceImpl;
import org.activityinfo.ui.store.remote.client.RestEndpoint;
import org.activityinfo.ui.vdom.client.VDomWidget;

public class AppEntryPoint implements EntryPoint, StoreChangeListener {

    public static RemoteStoreServiceImpl service;
    private VDomWidget widget;
    private Application app;

    @Override
    public void onModuleLoad() {

        service = new RemoteStoreServiceImpl(
                new RestEndpoint("/service/store"));

        app = new Application(service);

        // Ensure that GWT's event system gets initialized
        RootPanel rootPanel = RootPanel.get();

        widget = new VDomWidget(Chrome.mainSection(app));
        rootPanel.add(widget);

        app.getStoreEventBus().addChangeListener(this);

        // load the workspaces
        app.getWorkspaceStore().load();

        initializeRoute(app);

        hidePreloader();
    }

    private void initializeRoute(final Application app) {
        app.getRouter().updatePath(History.getToken());

        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                app.getRouter().updatePath(event.getValue());
            }
        });
    }

    private void hidePreloader() {
        Effects.fadeOut("status", 400);
        Effects.firstWait(350).thenFadeOut("preloader").thenWait(350).then(new Runnable() {
            @Override
            public void run() {
                Document.get().getBody().getStyle().setProperty("overflow", "visible");
            }
        });
    }

    @Override
    public void onStoreChanged(Store store) {
        widget.update(Chrome.mainSection(app));
    }
}
