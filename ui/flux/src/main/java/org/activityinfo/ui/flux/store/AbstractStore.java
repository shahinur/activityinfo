package org.activityinfo.ui.flux.store;

import org.activityinfo.ui.flux.dispatcher.Dispatcher;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractStore implements Store {

    private static final Logger LOGGER = Logger.getLogger(StoreEventBus.class.getName());

    private int storeId;

    private Set<StoreChangeListener> listeners = null;

    protected AbstractStore(Dispatcher dispatcher) {
        storeId = dispatcher.register(this);
    }

    @Override
    public final int getStoreId() {
        return storeId;
    }

    public final void addChangeListener(@Nonnull StoreChangeListener listener) {
        if(listeners == null) {
            listeners = new HashSet<>();
        }
        boolean wasNew = listeners.add(listener);
        //assert wasNew : "Listener was already registered";
    }

    public final void removeChangeListener(@Nonnull StoreChangeListener listener) {
        // assert listeners != null && listeners.contains(listener) : "Listener is not registered";

        if(listeners != null) {
            listeners.remove(listener);
        }
    }

    protected final void fireChange() {

        LOGGER.fine(getClass().getSimpleName() + ".fireChange()");

        if(listeners != null) {
            for(StoreChangeListener listener : listeners) {
                LOGGER.fine(getClass().getSimpleName() + " notifying " +
                    listener.getClass().getSimpleName() + " of change");

                try {
                    listener.onStoreChanged(this);
                } catch(Throwable caught) {
                    LOGGER.log(Level.SEVERE, "Exception thrown while firing change events for Store " + this, caught);
                }
            }
        }
    }
}
