package org.activityinfo.ui.app.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.RootPanel;
import org.activityinfo.ui.app.client.chrome.Chrome;
import org.activityinfo.ui.app.client.effects.Effects;
import org.activityinfo.ui.app.client.page.WindowLocationHash;
import org.activityinfo.ui.app.client.request.FetchWorkspaces;
import org.activityinfo.ui.store.remote.client.RemoteStoreServiceImpl;
import org.activityinfo.ui.store.remote.client.RestEndpoint;
import org.activityinfo.ui.vdom.client.VDomWidget;
import org.activityinfo.ui.vdom.shared.VDomLogger;

public class AppEntryPoint implements EntryPoint {

    public static RemoteStoreServiceImpl service;
    private VDomWidget widget;
    private Application app;

    @Override
    public void onModuleLoad() {

        VDomLogger.STD_OUT = true;
        VDomLogger.ENABLED = true;

        service = new RemoteStoreServiceImpl(
                new RestEndpoint("/service/store"));

        app = new Application(service);

        // Create a "view" on the location.hash
        WindowLocationHash location = new WindowLocationHash(app);
        location.start();

        // Ensure that GWTs event system gets initialized
        RootPanel rootPanel = RootPanel.get();
        widget = new VDomWidget(Chrome.mainSection(app));
        rootPanel.add(widget);


        // load the workspaces
        app.getRequestDispatcher().execute(new FetchWorkspaces());

        hidePreloader();
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
}
