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
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;

/**
 * @author yuriyz on 9/8/14.
 */
public class ConnectivityStore extends AbstractStore implements UpdateConnectivityHandler {

    private ConnectivityState state = ConnectivityState.OFFLINE;

    public ConnectivityStore(Dispatcher dispatcher) {
        super(dispatcher);

        if (GWT.isClient()) {
            listenConnectivityEvents();
        }
    }

    public native void listenConnectivityEvents() /*-{

        function onOnline(event) {
            $entry(this.@org.activityinfo.ui.app.client.chrome.connectivity.ConnectivityStore::setOnline())
        }

        function onOffline(event) {
            $entry(this.@org.activityinfo.ui.app.client.chrome.connectivity.ConnectivityStore::setOffline())
        }

        $wnd.addEventListener('online',  onOnline);
        $wnd.addEventListener('offline', onOffline);

    }-*/;

    public ConnectivityState getState() {
        return state;
    }

    public boolean isOnline() {
        return state == ConnectivityState.ONLINE;
    }

    @Override
    public void setState(ConnectivityState state) {
        this.state = state;
        fireChange();
    }

    public void setOnline() {
        setState(ConnectivityState.ONLINE);
    }

    public void setOffline() {
        setState(ConnectivityState.OFFLINE);
    }
}
