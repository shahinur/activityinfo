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

import org.activityinfo.ui.app.client.action.RemoteUpdateHandler;
import org.activityinfo.ui.app.client.request.Request;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.flux.store.AbstractStore;
import org.activityinfo.ui.store.remote.client.StatusCodeException;

/**
 * @author yuriyz on 9/8/14.
 */
public class ConnectivityStore extends AbstractStore implements RemoteUpdateHandler {

    // Default to online, as the client has some how managed to load the javascript application!
    private ConnectivityState state = ConnectivityState.ONLINE;

    public ConnectivityStore(Dispatcher dispatcher) {
        super(dispatcher);
    }

    public ConnectivityState getState() {
        return state;
    }

    public boolean isOnline() {
        return state == ConnectivityState.ONLINE;
    }


    @Override
    public void requestStarted(Request request) {

    }

    @Override
    public void requestFailed(Request request, Throwable e) {
        if(isConnectionProblem(e)) {
            this.state = ConnectivityState.OFFLINE;
        }
    }

    private boolean isConnectionProblem(Throwable e) {
        return !(e instanceof StatusCodeException);
    }

    @Override
    public <R> void processUpdate(Request<R> request, R response) {

    }
}
