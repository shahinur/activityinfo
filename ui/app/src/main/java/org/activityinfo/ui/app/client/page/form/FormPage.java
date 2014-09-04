package org.activityinfo.ui.app.client.page.form;

import com.google.gwt.safehtml.shared.SafeUri;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.flux.store.StoreEventBus;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class FormPage extends VComponent<FormPage> {

    private FormClass formClass;

    private FormViewType viewType = FormViewType.OVERVIEW;

    private StoreEventBus eventBus;

    private Promise<FormTree> formTree;

    public FormPage(Application application, Resource node) {
        this.formClass = FormClass.fromResource(node);
    }

    public ResourceId getResourceId() {
        return formClass.getId();
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
        return formClass;
    }

    @Override
    protected VTree render() {
        return null;
    }
}
