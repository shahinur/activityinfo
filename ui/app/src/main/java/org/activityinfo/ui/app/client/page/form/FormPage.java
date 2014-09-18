package org.activityinfo.ui.app.client.page.form;

import com.google.gwt.safehtml.shared.SafeUri;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.PageView;
import org.activityinfo.ui.app.client.page.PageViewFactory;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.store.FormState;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.flux.store.StoreEventBus;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class FormPage extends PageView {

    public static class Factory implements PageViewFactory<FormPlace> {

        private final Application application;

        public Factory(Application application) {
            this.application = application;
        }

        @Override
        public boolean accepts(Place place) {
            return place instanceof FormPlace;
        }

        @Override
        public PageView create(FormPlace place) {
            return new FormPage(application, place.getResourceId());
        }
    }

    private FormState formDraft;

    private FormViewType viewType = FormViewType.OVERVIEW;

    private StoreEventBus eventBus;

    private Promise<FormTree> formTree;

    public FormPage(Application application, ResourceId resourceId) {
        this.formDraft = application.getDraftStore().getFormDraft();
    }

    public ResourceId getResourceId() {
        return formDraft.getFormClass().getId();
    }


    public FormViewType getViewType() {
        return viewType;
    }

    private VTree getComponent() {
        switch(getViewType()) {
            case DESIGN:
                return new FormDesignerWidget(this);

            default:
            case OVERVIEW:
            case TABLE:
                return new FormTableWidget(this);
        }
    }


    public SafeUri viewUri(FormViewType view) {
        return Router.uri(new FormPlace(getResourceId(), view));
    }

    public FormClass getFormClass() {
        return formDraft.getFormClass();
    }

    @Override
    protected VTree render() {
        return null;
    }
}
