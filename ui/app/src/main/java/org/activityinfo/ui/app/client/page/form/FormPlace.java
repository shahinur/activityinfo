package org.activityinfo.ui.app.client.page.form;

import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.PlaceParser;

public class FormPlace implements Place {

    private ResourceId resourceId;
    private FormViewType formViewType;

    public FormPlace(ResourceId resourceId, FormViewType formViewType) {
        this.resourceId = resourceId;
        this.formViewType = formViewType;
    }

    @Override
    public String[] getPath() {
        return new String[]{"form", resourceId.asString(), formViewType.name().toLowerCase()};
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
}
