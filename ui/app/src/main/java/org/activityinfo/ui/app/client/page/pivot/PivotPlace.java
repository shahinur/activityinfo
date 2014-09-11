package org.activityinfo.ui.app.client.page.pivot;

import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.PlaceParser;

public class PivotPlace implements Place {
    @Override
    public String[] getPath() {
        return new String[] { "cube" };
    }

    @Override
    public void navigateTo(Application application) {
        application.getRouter().navigate(this);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof PivotPlace;
    }

    public static class Parser implements PlaceParser {
        @Override
        public Place tryParse(String[] path) {
            if(path.length >= 1 && path[0].equals("cube")) {
                return new PivotPlace();
            } else {
                return null;
            }
        }
    }


}
