package org.activityinfo.ui.app.client.page.folder;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.action.UpdatePlace;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.PlaceParser;
import org.activityinfo.ui.app.client.request.FetchFolder;

import javax.annotation.Nonnull;

public class FolderPlace implements Place {

    @Nonnull
    private final ResourceId resourceId;
    @Nonnull
    private final FolderPlaceType type;

    public FolderPlace(@Nonnull ResourceId resourceId, FolderPlaceType type) {
        this.resourceId = resourceId;
        this.type = type;
    }

    @Nonnull
    public FolderPlaceType getType() {
        return type;
    }

    @Override
    public String[] getPath() {
        return new String[] {type.getValue(), resourceId.asString() };
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    @Override
    public void navigateTo(Application application) {
        if(application.getFolderStore().get(resourceId).requiresFetch()) {
            application.getRequestDispatcher().execute(new FetchFolder(resourceId));
        }
        application.getDispatcher().dispatch(new UpdatePlace(this));
    }

    public static class Parser implements PlaceParser {

        @Override
        public Place tryParse(String[] path) {
            if(path.length >= 2 && ("folder".equalsIgnoreCase(path[0]) || "workspace".equalsIgnoreCase(path[0]))) {
                return new FolderPlace(ResourceId.valueOf(path[1]), FolderPlaceType.fromValue(path[0]));
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "FolderPlace{" +
            "resourceId=" + resourceId +
            "type=" + type +
            '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FolderPlace that = (FolderPlace) o;

        if (resourceId != null ? !resourceId.equals(that.resourceId) : that.resourceId != null) return false;
        return type == that.type;

    }

    @Override
    public int hashCode() {
        int result = resourceId != null ? resourceId.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
