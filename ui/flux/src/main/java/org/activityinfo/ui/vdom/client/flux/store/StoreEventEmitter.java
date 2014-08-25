package org.activityinfo.ui.vdom.client.flux.store;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class StoreEventEmitter {

    private static final Logger LOGGER = Logger.getLogger(StoreEventEmitter.class.getName());

    private Set<StoreChangeListener> listeners = null;

    public void addChangeListener(@Nonnull StoreChangeListener listener) {
        if(listeners == null) {
            listeners = new HashSet<>();
        }
        boolean wasNew = listeners.add(listener);
   //     assert wasNew : "Listener was already registered";
    }

    public void removeChangeListener(@Nonnull StoreChangeListener listener) {
       // assert listeners != null && listeners.contains(listener) : "Listener is not registered";

        if(listeners != null) {
           listeners.remove(listener);
        }
    }

    public void fireChange(Store store) {
        if(listeners != null) {
            for(StoreChangeListener listener : listeners) {
                try {
                    listener.onStoreChanged(store);
                } catch(Throwable caught) {
                    LOGGER.log(Level.SEVERE, "Exception thrown while firing change events for Store " + store, caught);
                }
            }
        }
    }

    public void stop() {
        listeners.clear();
    }
}
