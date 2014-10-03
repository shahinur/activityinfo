package org.activityinfo.ui.app.client.chrome.connectivity;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Timer;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.request.Ping;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;

/**
 * @author yuriyz on 9/10/14.
 */
public class ConnectivitySensor implements StoreChangeListener {

    private static final int PING_INTERVAL_MS = 5000;

    private final Application application;
    private Timer timer;

    public ConnectivitySensor(Application application) {
        this.application = application;
        start();
    }

    public void start() {
        if (GWT.isClient()) {
            listenConnectivityEvents();
        }
        application.getConnectivityStore().addChangeListener(this);
    }

    public native void listenConnectivityEvents() /*-{

        function onOnline(event) {
            $entry(this.@org.activityinfo.ui.app.client.chrome.connectivity.ConnectivitySensor::setOnline())
        }

        function onOffline(event) {
            $entry(this.@org.activityinfo.ui.app.client.chrome.connectivity.ConnectivitySensor::setOffline())
        }

        $wnd.addEventListener('online', onOnline);
        $wnd.addEventListener('offline', onOffline);

    }-*/;

    public void setOnline() {
        application.getDispatcher().dispatch(new UpdateConnectivityAction(ConnectivityState.ONLINE));
    }

    public void setOffline() {
        application.getDispatcher().dispatch(new UpdateConnectivityAction(ConnectivityState.OFFLINE));
    }

    @Override
    public void onStoreChanged(Store store) {
        if(application.getConnectivityStore().isOnline()) {
            if(isTimerRunning()) {
                timer.cancel();
            }
        } else {
            if (!isTimerRunning()) {
                timer = new Timer() {
                    @Override
                    public void run() {
                        application.getRequestDispatcher().execute(new Ping());
                    }
                };
                timer.scheduleRepeating(PING_INTERVAL_MS);
            }
        }
    }

    private boolean isTimerRunning() {
        return timer != null && timer.isRunning();
    }

}
