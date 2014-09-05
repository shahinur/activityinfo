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

    public FolderPlace(@Nonnull ResourceId resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String[] getPath() {
        return new String[] { "folder", resourceId.asString() };
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
            if(path.length >= 2 && "folder".equals(path[0])) {
                return new FolderPlace(ResourceId.valueOf(path[1]));
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return "FolderPlace{" +
            "resourceId=" + resourceId +
            '}';
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FolderPlace that = (FolderPlace) o;

        if (!resourceId.equals(that.resourceId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return resourceId.hashCode();
    }
}
