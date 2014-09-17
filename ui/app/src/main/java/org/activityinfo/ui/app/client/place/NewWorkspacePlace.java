package org.activityinfo.ui.app.client.place;

import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.action.UpdatePlace;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.PlaceParser;

import java.util.Arrays;

public class NewWorkspacePlace implements Place {


    @Override
    public String[] getPath() {
        return new String[] { "create", "workspace"};
    }

    @Override
    public void navigateTo(Application application) {
        application.getDispatcher().dispatch(new UpdatePlace(this));
    }

    @Override
    public String toString() {
        return Arrays.toString(getPath());
    }

    public static class Parser implements PlaceParser {

        @Override
        public Place tryParse(String[] path) {
            if(path != null && path.length >= 2 &&
                "create".equalsIgnoreCase(path[0]) &&
                "workspace".equalsIgnoreCase(path[1])) {
                return new NewWorkspacePlace();
            } else {
                return null;
            }
        }
    }
}
