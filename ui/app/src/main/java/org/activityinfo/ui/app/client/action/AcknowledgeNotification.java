package org.activityinfo.ui.app.client.action;

import org.activityinfo.ui.flux.action.Action;
import org.activityinfo.ui.flux.store.Store;

public class AcknowledgeNotification implements Action {

    private String id;

    public AcknowledgeNotification(String id) {
        this.id = id;
    }

    @Override
    public void accept(Store store) {
        if(store instanceof NotificationHandler) {
            ((NotificationHandler) store).notificationAcknowledged(id);
        }
    }
}
