package org.activityinfo.ui.app.client.place;

import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.action.UpdatePlace;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.PlaceParser;

import javax.annotation.Nonnull;

public class NewResourcePlace implements Place {

    @Nonnull
    private final ResourceType resourceType;
    @Nonnull
    private final String[] path;
    private final String pathAsString;

    public NewResourcePlace(@Nonnull ResourceType resourceType) {
        this.resourceType = resourceType;
        this.path = new String[]{"create", resourceType.getValue()};
        this.pathAsString = "/create/" + resourceType.getValue();
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    @Override
    public String[] getPath() {
        return path;
    }

    @Override
    public void navigateTo(Application application) {
        application.getDispatcher().dispatch(new UpdatePlace(this));
    }

    @Override
    public String toString() {
        return pathAsString;
    }

    public static class Parser implements PlaceParser {

        @Override
        public Place tryParse(String[] path) {
            if(path != null && path.length >= 2 && "create".equalsIgnoreCase(path[0])) {
                ResourceType resourceType = ResourceType.fromValueSilently(path[1]);
                if (resourceType != null) {
                    return new NewResourcePlace(resourceType);
                }
            }
            return null;
        }
    }


}
