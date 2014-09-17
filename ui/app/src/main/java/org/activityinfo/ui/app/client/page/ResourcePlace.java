package org.activityinfo.ui.app.client.page;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.folder.FolderPlace;

import javax.annotation.Nonnull;

public class ResourcePlace implements Place {

    @Nonnull
    private final ResourceId resourceId;

    public ResourcePlace(@Nonnull ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String[] getPath() {
        return new String[] { "resource", resourceId.asString() };
    }

    @Override
    public void navigateTo(Application application) {
        application.getRouter().navigate(this);
    }

    @Nonnull
    public ResourceId getResourceId() {
        return resourceId;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourcePlace;
    }

    public static class Parser implements PlaceParser {

        @Override
        public Place tryParse(String[] path) {
            if(path.length >= 2 && "resource".equals(path[0])) {
                return new ResourcePlace(ResourceId.valueOf(path[1]));
            }
            return null;
        }
    }
}
