package org.activityinfo.ui.app.client.page;

import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.draft.Draft;
import org.activityinfo.ui.app.client.page.pivot.PivotPage;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.Objects;

import static org.activityinfo.ui.vdom.shared.html.H.p;

public class ResourcePageView extends PageView implements StoreChangeListener {

    private final Application application;

    private ResourceId currentResourceId;
    private VTree view = new VNode(HtmlTag.NOSCRIPT);

    public ResourcePageView(Application application) {
        this.application = application;
    }

    @Override
    public boolean accepts(Place place) {
        return place instanceof ResourcePlace;
    }

    @Override
    protected void componentDidMount() {
        application.getRouter().addChangeListener(this);
        application.getResourceStore().addChangeListener(this);
        maybeRefresh();

    }

    @Override
    public void onStoreChanged(Store store) {
        maybeRefresh();
    }

    private void maybeRefresh() {
        if(!Objects.equals(currentResourceId, getCurrentResourceId())) {
            this.currentResourceId = getCurrentResourceId();
            this.view = createView(currentResourceId);
            this.refresh();
        }
    }

    @Override
    protected void componentWillUnmount() {
        application.getRouter().removeChangeListener(this);
        application.getResourceStore().removeChangeListener(this);
    }

    private ResourceId getCurrentResourceId() {
        if (application.getRouter().getCurrentPlace() instanceof ResourcePlace) {
            ResourcePlace place = application.getRouter().getCurrentPlace();
            return place.getResourceId();
        } else {
            return null;
        }
    }

    @Override
    protected VTree render() {
        return view;
    }

    private VTree createView(ResourceId resourceId) {
        if(resourceId == null) {
            return new VNode(HtmlTag.NOSCRIPT);
        }
        if(application.getDraftStore().hasDraft(resourceId)) {
            Status<Draft> draft = application.getDraftStore().get(resourceId);
            if(draft.isAvailable()) {
                return createPageView(draft.get().getResource());
            }
        }
        return new PagePreLoader();
    }

    private VTree createPageView(Resource resource) {
        ResourceId classId = ResourceId.valueOf(resource.getString("classId"));

        if(classId.equals(PivotTableModel.CLASS_ID)) {
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
