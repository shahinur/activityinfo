package org.activityinfo.ui.flux.store;

import java.util.List;

/**
 * Marker interface for components which depend on
 * store change events
 */
public interface IsStoreDependent {

    /**
     *
     * @return the list of stores on which this
     * sink depends and for which it will receives events
     */
    List<Store> getDependencies();

}
