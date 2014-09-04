package org.activityinfo.ui.app.client.page.home;

import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.Place;

public enum HomePlace implements Place {

    INSTANCE;

    @Override
    public String[] getPath() {
        return new String[0];
    }

    @Override
    public void navigateTo(Application application) {
        application.getRouter().navigate(this);
    }

    @Override
    public String toString() {
        return "/home";
    }
}
