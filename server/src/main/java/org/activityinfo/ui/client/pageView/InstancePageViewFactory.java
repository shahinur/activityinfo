package org.activityinfo.ui.client.pageView;

import com.google.common.base.Function;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.client.EventBus;
import org.activityinfo.ui.client.page.instance.InstancePage;
import org.activityinfo.ui.client.pageView.formClass.DesignTab;
import org.activityinfo.ui.client.pageView.formClass.FormClassPageView;
import org.activityinfo.ui.client.pageView.formClass.TableTab;
import org.activityinfo.ui.client.widget.DisplayWidget;

/**
 * Creates a InstancePageView given a class id
 */
public class InstancePageViewFactory implements Function<InstanceViewModel, DisplayWidget<InstanceViewModel>> {

    private final ResourceLocator resourceLocator;
    private final EventBus eventBus;

    public InstancePageViewFactory(ResourceLocator resourceLocator, EventBus eventBus) {
        this.resourceLocator = resourceLocator;
        this.eventBus = eventBus;
    }

    @Override
    public InstancePageView apply(InstanceViewModel view) {
        ResourceId classId = view.getInstance().getClassId();
        if (classId.equals(FormClass.CLASS_ID)) {
            if (view.getPageId() == InstancePage.DESIGN_PAGE_ID) {
                return new FormClassPageView(new DesignTab(resourceLocator), eventBus);
            } else if (view.getPageId() == InstancePage.TABLE_PAGE_ID) {
                return new FormClassPageView(new TableTab(resourceLocator), eventBus);
            } else {
                throw new UnsupportedOperationException("Unknown page id:" + view.getPageId());
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
