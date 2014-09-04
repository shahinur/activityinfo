package org.activityinfo.ui.app.client.page;

import org.activityinfo.ui.app.client.Application;

/**
 * Describes a specific location within the application,
 * reachable by a URL
 */
public interface Place {

    String[] getPath();

    /**
     * Initiates a navigation to this {@code Place}
     */
    void navigateTo(Application application);

}
