package org.activityinfo.ui.app.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.ui.RootPanel;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.app.client.chrome.Chrome;
import org.activityinfo.ui.app.client.chrome.connectivity.ConnectivitySensor;
import org.activityinfo.ui.app.client.chrome.tasks.TaskSensor;
import org.activityinfo.ui.app.client.effects.Effects;
import org.activityinfo.ui.app.client.page.WindowLocationHash;
import org.activityinfo.ui.app.client.request.FetchWorkspaces;
import org.activityinfo.ui.store.remote.client.RemoteStoreServiceImpl;
import org.activityinfo.ui.store.remote.client.RestEndpoint;
import org.activityinfo.ui.vdom.client.VDomWidget;
import org.activityinfo.ui.vdom.shared.VDomLogger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AppEntryPoint implements EntryPoint {

    private static final Logger LOGGER = Logger.getLogger("");

    public static Application app;
    public static RemoteStoreServiceImpl service;
    private VDomWidget widget;

    @Override
    public void onModuleLoad() {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
            public void onUncaughtException(Throwable e) {
                Throwable throwable = unwrap(e);
                LOGGER.log(Level.FINE, throwable.getMessage(), throwable);
            }
        });

        // load module via deffered command to make sure UncaughtExceptionHandler handles all exceptions
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onModuleLoad2();
            }
        });
    }

    public void onModuleLoad2() {

        VDomLogger.STD_OUT = true;

        I18N.init();

        service = new RemoteStoreServiceImpl(
                new RestEndpoint("/service"));

        app = new Application(service);

        // Create a "view" on the location.hash
        WindowLocationHash location = new WindowLocationHash(app);
        location.start();

        TaskSensor taskSensor = new TaskSensor(app);
        taskSensor.start();

        // view to track online/offline state from browser
        new ConnectivitySensor(app);

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

    public Throwable unwrap(Throwable e) {
        if(e instanceof UmbrellaException) {
            UmbrellaException ue = (UmbrellaException) e;
            if(ue.getCauses().size() == 1) {
                return unwrap(ue.getCauses().iterator().next());
            }
        }
        return e;
    }
}
