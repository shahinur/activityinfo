package org.activityinfo.ui.app.client.action;

public interface NotificationHandler {

    /**
     * Updates the store with the users acknowledgement of the given object.
     * @param id
     */
    void notificationAcknowledged(String id);
}
