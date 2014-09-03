package org.activityinfo.ui.app.client.page.create;

import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.PlaceParser;

import java.util.Arrays;

public class NewWorkspacePlace implements Place {

    public static final String[] PATH = new String[]{"create", "workspace"};

    @Override
    public String[] getPath() {
        return PATH;
    }

    public static class Parser implements PlaceParser {

        @Override
        public Place tryParse(String[] path) {
            if(Arrays.equals(PATH, path)) {
                return new NewWorkspacePlace();
            }
            return null;
        }
    }
}
