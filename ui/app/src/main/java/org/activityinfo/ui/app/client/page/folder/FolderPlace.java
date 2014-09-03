package org.activityinfo.ui.app.client.page.folder;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.PlaceParser;

public class FolderPlace implements Place {

    private final ResourceId resourceId;

    public FolderPlace(ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String[] getPath() {
        return new String[] { "folder", resourceId.asString() };
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    public static class Parser implements PlaceParser {

        @Override
        public Place tryParse(String[] path) {
            if(path.length >= 2 && "folder".equals(path[0])) {
                return new FolderPlace(ResourceId.valueOf(path[1]));
            }
            return null;
        }
    }
}
