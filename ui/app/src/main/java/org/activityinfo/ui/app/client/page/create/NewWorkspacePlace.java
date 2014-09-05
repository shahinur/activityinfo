package org.activityinfo.ui.app.client.page.create;

import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.action.UpdatePlace;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.PlaceParser;

import java.util.Arrays;

public enum NewWorkspacePlace implements Place {

    INSTANCE;

    public static final String[] PATH = new String[]{"create", "workspace"};

    @Override
    public String[] getPath() {
        return PATH;
    }

    @Override
    public void navigateTo(Application application) {
        application.getDispatcher().dispatch(new UpdatePlace(this));
    }


    @Override
    public String toString() {
        return "/create/workspace";
    }

    public static class Parser implements PlaceParser {

        @Override
        public Place tryParse(String[] path) {
            if(Arrays.equals(PATH, path)) {
                return INSTANCE;
            }
            return null;
        }
    }


}
