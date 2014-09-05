package org.activityinfo.ui.app.client.page.form;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.action.UpdatePlace;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.PlaceParser;

import javax.annotation.Nonnull;

public class FormPlace implements Place {

    private final ResourceId resourceId;
    private final FormViewType formViewType;

    public FormPlace(@Nonnull ResourceId resourceId, @Nonnull FormViewType formViewType) {
        this.resourceId = resourceId;
        this.formViewType = formViewType;
    }

    @Override
    public String[] getPath() {
        return new String[]{"form", resourceId.asString(), formViewType.name().toLowerCase()};
    }

    @Override
    public void navigateTo(Application application) {
        application.getDispatcher().dispatch(new UpdatePlace(this));
    }

    public static class Parser implements PlaceParser {

        @Override
        public Place tryParse(String[] path) {
            if(path.length >= 2) {
                if(path[0].equals("form")) {
                    ResourceId id = ResourceId.valueOf(path[1]);
                    FormViewType type = FormViewType.TABLE;
                    if(path.length >= 3) {
                        type = FormViewType.valueOf(path[2].toUpperCase());
                    }
                    return new FormPlace(id, type);
                }
            }
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormPlace formPlace = (FormPlace) o;

        if (formViewType != formPlace.formViewType) return false;
        if (!resourceId.equals(formPlace.resourceId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = resourceId.hashCode();
        result = 31 * result + formViewType.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "/form/" + resourceId + "/" + formViewType;
    }
}
