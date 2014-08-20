package org.activityinfo.ui.app.client.page.form;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.app.client.page.Breadcrumb;
import org.activityinfo.ui.app.client.page.resource.ResourcePage;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.client.flux.store.LoadingStatus;
import org.activityinfo.ui.vdom.client.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.client.flux.store.StoreEventEmitter;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.List;
import java.util.Objects;

public class FormPage implements ResourcePage {

    private FormClass formClass;

    private FormViewType viewType = FormViewType.OVERVIEW;

    private StoreEventEmitter eventEmitter = new StoreEventEmitter();

    private Promise<FormTree> formTree;

    public FormPage(Resource node) {
        this.formClass = FormClass.fromResource(node);
    }

    @Override
    public ResourceId getResourceId() {
        return formClass.getId();
    }

    @Override
    public boolean tryHandleNavigation(String[] path) {

        // resource/{id}/view
        if(path.length < 3) {
            navigate(FormViewType.TABLE);
        } else {
            switch(path[2]) {
                case "design":
                    navigate(FormViewType.DESIGN);
                    return true;
                case "table":
                    navigate(FormViewType.TABLE);
                    return true;
            }
        }
        return false;
    }

    private void navigate(FormViewType viewType) {
        if(!Objects.equals(viewType, this.viewType)) {
            this.viewType = viewType;
            eventEmitter.fireChange(this);
        }
    }

    public FormViewType getViewType() {
        return viewType;
    }

    public void setViewType(FormViewType viewType) {
        this.viewType = viewType;
    }

    @Override
    public String getPageTitle() {
        return formClass.getLabel();
    }

    @Override
    public String getPageDescription() {
        return formClass.getDescription();
    }

    @Override
    public Icon getPageIcon() {
        return FontAwesome.EDIT;
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        return null;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        eventEmitter.stop();
    }

    @Override
    public LoadingStatus getLoadingStatus() {
        return LoadingStatus.LOADED;
    }

    @Override
    public void addChangeListener(StoreChangeListener listener) {
        eventEmitter.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(StoreChangeListener listener) {
        eventEmitter.removeChangeListener(listener);
    }

    public SafeUri viewUri(FormViewType view) {
        return UriUtils.fromTrustedString("#resource/" + formClass.getId() + "/" + view.name().toLowerCase());
    }

    public FormClass getFormClass() {
        return formClass;
    }
}
