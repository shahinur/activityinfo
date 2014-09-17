package org.activityinfo.ui.app.client.store;

import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;

public class CountingStoreListener implements StoreChangeListener {
    private int changeCount = 0;

    @Override
    public void onStoreChanged(Store store) {
        changeCount++;
    }

    public int getChangeCount() {
        return changeCount;
    }
}
