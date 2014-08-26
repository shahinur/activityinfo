package org.activityinfo.ui.app.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import org.activityinfo.ui.app.client.chrome.Chrome;
import org.activityinfo.ui.app.client.effects.Effects;
import org.activityinfo.ui.app.client.page.PageStore;
import org.activityinfo.ui.app.client.page.resource.ResourcePageContainer;
import org.activityinfo.ui.app.client.store.AppStores;
import org.activityinfo.ui.store.remote.client.RemoteStoreServiceImpl;
import org.activityinfo.ui.store.remote.client.RestEndpoint;
import org.activityinfo.ui.vdom.client.VDomWidget;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;

public class AppEntryPoint implements EntryPoint, StoreChangeListener {

    public static RemoteStoreServiceImpl service;
    private VDomWidget widget;
    private AppStores app;

    @Override
    public void onModuleLoad() {

        service = new RemoteStoreServiceImpl(
                new RestEndpoint("/service/store"));

        app = new AppStores(service);

        // Ensure that GWT's event system gets initialized
        RootPanel rootPanel = RootPanel.get();

        widget = new VDomWidget(Chrome.mainSection(app));
        rootPanel.add(widget);

        // hack event handling for the moment
        app.getWorkspaceStore().addChangeListener(this);
        app.getRouter().addChangeListener(this);

        // load the workspaces
        app.getWorkspaceStore().load();

        initializeRoute(app);

        hidePreloader();
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
        PageStore activePage = app.getRouter().getActivePage();
        activePage.addChangeListener(this);
        if(activePage instanceof ResourcePageContainer) {
            ResourcePageContainer container = (ResourcePageContainer) activePage;
            if(container.getPage() != null) {
                container.getPage().addChangeListener(this);
            }
        }
        widget.update(Chrome.mainSection(app));
    }
}
