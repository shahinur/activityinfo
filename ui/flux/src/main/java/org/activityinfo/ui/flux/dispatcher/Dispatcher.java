package org.activityinfo.ui.flux.dispatcher;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.ui.flux.action.Action;
import org.activityinfo.ui.flux.store.Store;

import java.util.List;
import java.util.Map;

/**
 * The dispatcher is the central hub that manages all data flow in a Flux application. It is essentially a registry
 * of callbacks into the stores. Each store registers itself and provides a callback. When the dispatcher responds to
 * an action, all stores in the application are sent the data payload provided by the action via the callbacks in the
 * registry.
 * <p/>
 * <p>As an application grows, the dispatcher becomes more vital, as it can manage dependencies between stores by
 * invoking the registered callbacks in a specific order. Stores can declaratively wait for other stores to finish
 * updating, and then update themselves accordingly.
 */
public class Dispatcher {

    private int lastId = 1;

    private Map<Integer, Store> callbacks = Maps.newHashMap();
    private Map<Integer, Boolean> pending = Maps.newHashMap();
    private Map<Integer, Boolean> handled = Maps.newHashMap();

    private boolean dispatching;

    private Action pendingPayload;

    /**
     * Register a Store's callback so that it may be invoked by an action.
     *
     * @param store The callback to be registered.
     * @return The index of the store within the callback array
     */
    public int register(Store store) {
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

        for (int ii = 0; ii < ids.length; ii++) {
            int id = ids[ii];
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
        pendingPayload.accept(callbacks.get(id));
        handled.put(id, Boolean.TRUE);
    }

    public void unregister(int id) {
        assert callbacks.containsKey(id) : id + " does not map to a registered callback";

        callbacks.remove(id);
    }

    public void dispatch(Action<?> action) {
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

    private void startDispatching(Action<?> payload) {
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
