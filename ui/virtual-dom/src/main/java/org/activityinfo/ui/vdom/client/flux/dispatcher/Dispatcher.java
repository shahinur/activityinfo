package org.activityinfo.ui.vdom.client.flux.dispatcher;

import org.activityinfo.ui.vdom.client.flux.action.Action;
import org.activityinfo.ui.vdom.client.flux.store.Store;

import java.util.ArrayList;
import java.util.List;

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

    private List<Store> stores = new ArrayList<>();

    /**
     * Register a Store's callback so that it may be invoked by an action.
     *
     * @param store The callback to be registered.
     * @return The index of the store within the callback array
     */
    public int register(Store store) {
        assert !stores.contains(store) : "Store already registered";
        stores.add(store);
        return stores.size() - 1;
    }

    public void dispatch(Action<?> action) {

    }

}
