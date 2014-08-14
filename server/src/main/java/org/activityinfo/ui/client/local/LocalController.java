package org.activityinfo.ui.client.local;

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

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import org.activityinfo.i18n.shared.UiConstants;
import org.activityinfo.legacy.client.Dispatcher;
import org.activityinfo.legacy.client.remote.AbstractDispatcher;
import org.activityinfo.legacy.client.remote.Remote;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.legacy.shared.command.Command;
import org.activityinfo.legacy.shared.command.result.CommandResult;
import org.activityinfo.ui.client.AppEvents;
import org.activityinfo.ui.client.EventBus;
import org.activityinfo.ui.client.inject.ClientSideAuthProvider;
import org.activityinfo.ui.client.local.LocalStateChangeEvent.State;
import org.activityinfo.ui.client.local.capability.LocalCapabilityProfile;
import org.activityinfo.ui.client.local.capability.PermissionRefusedException;
import org.activityinfo.ui.client.local.sync.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * This class keeps as much of the offline functionality behind a runAsync
 * clause to defer downloading the related JavaScript until the user actually
 * goes into offline mode.
 */
@Singleton
public class LocalController extends AbstractDispatcher {

    public interface PromptConnectCallback {
        void onCancel();

        void onTryToConnect();
    }

    private final EventBus eventBus;
    private final Provider<Synchronizer> synchronizerProvider;
    private UiConstants uiConstants;
    private final Dispatcher remoteDispatcher;
    private final LocalCapabilityProfile capabilityProfile;
    private final Provider<SyncHistoryTable> historyTable;

    private Strategy activeStrategy;
    private Date lastSynced = null;

    @Inject
    public LocalController(EventBus eventBus,
                           @Remote Dispatcher remoteDispatcher,
                           Provider<Synchronizer> gateway,
                           LocalCapabilityProfile capabilityProfile,
                           UiConstants uiConstants,
                           Provider<SyncHistoryTable> historyTable) {
        this.eventBus = eventBus;
        this.remoteDispatcher = remoteDispatcher;
        this.synchronizerProvider = gateway;
        this.capabilityProfile = capabilityProfile;
        this.uiConstants = uiConstants;
        this.historyTable = historyTable;

        Log.trace("OfflineManager: starting");

        activateStrategy(new NotInstalledStrategy());

        eventBus.addListener(AppEvents.INIT, new Listener<BaseEvent>() {

            @Override
            public void handleEvent(BaseEvent be) {
                fireStatus();
            }
        });
    }

    public Date getLastSyncTime() {
        return lastSynced;
    }

    public void install() {
        Window.alert("Offline mode under reconstruction!");
    }

    public void synchronize() {
        Window.alert("Offline mode under reconstruction!");
    }

    public State getState() {
        return activeStrategy.getState();
    }


    @Override
    public <T extends CommandResult> void execute(Command<T> command, AsyncCallback<T> callback) {
        activeStrategy.dispatch(command, callback);
    }

    private void activateStrategy(Strategy strategy) {
        try {
            this.activeStrategy = strategy;
            this.activeStrategy.activate();
            fireStatus();

        } catch (Exception caught) {
            // errors really ought to be handled by the strategy that is passing
            // control to us
            // but we can't afford to let an uncaught exception go as it could
            // leave the app
            // in a state of limbo
            Log.error("Uncaught exception while activatign strategy, defaulting to Not INstalled");
            activateStrategy(new NotInstalledStrategy());
        }
    }

    private void fireStatus() {
        eventBus.fireEvent(new LocalStateChangeEvent(this.activeStrategy.getState()));
    }

    private void reportFailure(Throwable throwable) {
        Log.error("Exception in offline controller", throwable);

        eventBus.fireEvent(new SyncErrorEvent(SyncErrorType.fromException(throwable)));
    }

    private abstract class Strategy {
        Strategy activate() {
            return this;
        }

        void dispatch(Command command, AsyncCallback callback) {
            // by default, we send to the server
            remoteDispatcher.execute(command, callback);
        }

        abstract State getState();
    }

    /**
     * Strategy for handling the state in which offline mode is not at all
     * available.
     * <p/>
     * The only thing the user can do from here is start installation.
     */
    private class NotInstalledStrategy extends Strategy {

        @Override
        public NotInstalledStrategy activate() {
            return this;
        }

        @Override State getState() {
            return State.UNINSTALLED;
        }

    }

    private static class CommandRequest {
        private final Command command;
        private final AsyncCallback callback;

        public CommandRequest(Command command, AsyncCallback callback) {
            super();
            this.command = command;
            this.callback = callback;
        }

        public void dispatch(Strategy strategy) {
            strategy.dispatch(command, callback);
        }
    }

    private void doDispatch(final Collection<CommandRequest> requests) {
        if (!requests.isEmpty()) {
            // wait until everything's been switched around
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    for (CommandRequest request : requests) {
                        request.dispatch(activeStrategy);
                    }
                }
            });
        }
    }
}
