package org.activityinfo.ui.client.pageView;

import com.google.common.base.Function;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.core.shared.application.FolderClass;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.shared.adapter.FolderListAdapter;
import org.activityinfo.ui.client.page.instance.InstancePlace;
import org.activityinfo.ui.client.pageView.folder.FolderPageView;
import org.activityinfo.ui.client.pageView.formClass.FormClassDesignView;
import org.activityinfo.ui.client.pageView.formClass.FormClassPageView;
import org.activityinfo.ui.client.pageView.geodb.GeodbPageView;
import org.activityinfo.ui.client.widget.DisplayWidget;

/**
 * Creates a InstancePageView given a class id
 */
public class InstancePageViewFactory implements Function<FormInstance, DisplayWidget<FormInstance>> {

    private final ResourceLocator resourceLocator;
    private final InstancePlace place;

    public InstancePageViewFactory(ResourceLocator resourceLocator, InstancePlace place) {
        this.resourceLocator = resourceLocator;
        this.place = place;
    }

    @Override
    public InstancePageView apply(FormInstance instance) {
        if(instance.getId().equals(FolderListAdapter.GEODB_ID)) {
            return new GeodbPageView(resourceLocator);
        }

        ResourceId classId = instance.getClassId();
        if(classId.equals(FolderClass.CLASS_ID)) {
            return new FolderPageView(resourceLocator);
        } else if(classId.equals(FormClass.CLASS_ID)) {
            if("design".equals(place.getView())) {
                return new FormClassDesignView(resourceLocator);
            } else {
                 return new FormClassPageView(resourceLocator);
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
