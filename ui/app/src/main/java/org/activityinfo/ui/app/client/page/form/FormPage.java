package org.activityinfo.ui.app.client.page.form;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.page.Breadcrumb;
import org.activityinfo.ui.app.client.page.resource.ResourcePage;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.client.flux.store.LoadingStatus;
import org.activityinfo.ui.vdom.client.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.List;

public class FormPage implements ResourcePage {

    private FormClass formClass;


    public FormPage(Resource node) {
        this.formClass = FormClass.fromResource(node);
    }

    @Override
    public ResourceId getResourceId() {
        return formClass.getId();
    }

    @Override
    public boolean tryHandleNavigation(String[] path) {
        return false;
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

    }


    @Override
    public LoadingStatus getLoadingStatus() {
        return LoadingStatus.LOADED;
    }

    @Override
    public void addChangeListener(StoreChangeListener listener) {

    }

    @Override
    public void removeChangeListener(StoreChangeListener listener) {

    }

    public SafeUri viewUri(FormViewType view) {
        return UriUtils.fromTrustedString("#resource/" + formClass.getId() + "/" + view.name().toLowerCase());
    }
}
