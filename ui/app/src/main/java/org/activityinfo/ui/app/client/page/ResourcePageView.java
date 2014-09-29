package org.activityinfo.ui.app.client.page;

import com.google.common.base.Function;
import org.activityinfo.model.analysis.PivotTableModelClass;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.draft.Draft;
import org.activityinfo.ui.app.client.page.pivot.PivotPage;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.p;

public class ResourcePageView extends PageView implements StoreChangeListener {

    public static class Factory implements PageViewFactory<ResourcePlace> {

        private Application application;

        public Factory(Application application) {
            this.application = application;
        }

        @Override
        public boolean accepts(Place place) {
            return place instanceof ResourcePlace;
        }

        @Override
        public PageView create(ResourcePlace place) {
            return new ResourcePageView(application, place.getResourceId());
        }
    }

    private final Application application;
    private ResourceId resourceId;

    private Status<UserResource> resource;

    public ResourcePageView(Application application, ResourceId resourceId) {
        this.application = application;
        this.resourceId = resourceId;

        Status<Draft> draft = application.getDraftStore().get(resourceId);
        if(draft.isAvailable()) {
            this.resource = draft.join(new Function<Draft, UserResource>() {
                @Override
                public UserResource apply(Draft input) {
                    return input.getUserResource();
                }
            });
        } else {
            this.resource = application.getResourceStore().get(resourceId);
        }
    }

    @Override
    protected void componentDidMount() {
        application.getResourceStore().addChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        if(!resource.isAvailable()) {
            resource = application.getResourceStore().get(resourceId);
            refresh();
        }
    }

    @Override
    protected void componentWillUnmount() {
        application.getResourceStore().removeChangeListener(this);
    }

    @Override
    protected VTree render() {
        if(resource.isAvailable()) {
            return createPageView(resource.get().getResource());
        } else {
            return new PagePreLoader();
        }
    }

    private VTree createPageView(Resource resource) {
        ResourceId classId = resource.getValue().getClassId();

        if(classId.equals(PivotTableModelClass.CLASS_ID)) {
            Status<FormClass> formClass = application.getResourceStore().getFormClass(classId);
            if(formClass.isAvailable()) {
                return new PivotPage(application, formClass.get(), resource);
            }
            return new PagePreLoader();
        } else {
            return p("class = " + classId);
        }
    }
}
