package org.activityinfo.ui.flux.experiment.dispatcher;
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.ui.flux.experiment.payload.Payload;
import org.activityinfo.ui.flux.experiment.store.StoreCallback;

import java.util.List;
import java.util.Map;

/**
 * @author yuriyz on 9/8/14.
 */
public class Dispatcher {
    private int lastId = 1;

    private Map<Integer, StoreCallback> callbacks = Maps.newHashMap();
    private Map<Integer, Boolean> pending = Maps.newHashMap();
    private Map<Integer, Boolean> handled = Maps.newHashMap();

    private boolean dispatching;

    private Payload pendingPayload;

    /**
     * Register a Store's callback so that it may be invoked by an action.
     *
     * @param store The callback to be registered.
     * @return The index of the store within the callback array
     */
    public int register(StoreCallback store) {
        int id = lastId++;
        callbacks.put(id, store);
        return id;
    }

    public boolean isDispatching() {
        return dispatching;
    }

    /**
     * Waits for the callbacks specified to be invoked before continuing execution
     * of the current callback. This method should only be used by a callback in
     * response to a dispatched payload.
     *
     * @param ids the ids of the dispatchers for which we need to wait.
     */
    public void waitFor(int... ids) {
        assert isDispatching() : "Dispatcher.waitFor(): must be invoked while dispatching";

        for (int id : ids) {
            if (pending.get(id) == Boolean.TRUE) {
                assert handled.get(id) == Boolean.TRUE : "Circular dependency deteced while " +
                        "waiting for " + id;
                continue;
            }
            assert callbacks.containsKey(id) : id + " does not map to a registered callback";
            invokeCallback(id);
        }
    }

    private void invokeCallback(int id) {
        pending.put(id, Boolean.TRUE);
        callbacks.get(id).run(pendingPayload);
        handled.put(id, Boolean.TRUE);
    }

    public void unregister(int id) {
        assert callbacks.containsKey(id) : id + " does not map to a registered callback";

        callbacks.remove(id);
    }

    public void dispatch(Payload action) {
        assert !dispatching : "Cannot dispatch in the middle of a dispatch";
        startDispatching(action);
        try {

            // only dispatch to those that were registered at the time of dispatch
            List<Integer> registeredCallbacks = Lists.newArrayList(callbacks.keySet());

            for(Integer id : registeredCallbacks) {
                if (pending.get(id) == Boolean.TRUE) {
                    continue;
                }
                invokeCallback(id);
            }
        } finally {
            stopDispatching();
        }
    }

    private void startDispatching(Payload payload) {
        for (Integer id : callbacks.keySet()) {
            pending.remove(id);
            handled.remove(id);
        }
        pendingPayload = payload;
        dispatching = true;
    }

    private void stopDispatching() {
        pendingPayload = null;
        dispatching = false;
    }
}
