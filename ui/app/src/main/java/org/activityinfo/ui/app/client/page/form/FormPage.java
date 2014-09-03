package org.activityinfo.ui.app.client.page.form;

import com.google.gwt.safehtml.shared.SafeUri;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.app.client.page.Breadcrumb;
import org.activityinfo.ui.app.client.page.PageStore;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.flux.store.LoadingStatus;
import org.activityinfo.ui.flux.store.StoreEventBus;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;
import java.util.Objects;

import static org.activityinfo.ui.vdom.shared.html.H.div;

public class FormPage implements PageStore {

    private FormClass formClass;

    private FormViewType viewType = FormViewType.OVERVIEW;

    private StoreEventBus eventBus;

    private Promise<FormTree> formTree;

    public FormPage(StoreEventBus eventBus, Resource node) {
        this.eventBus = eventBus;
        this.formClass = FormClass.fromResource(node);
    }

    public ResourceId getResourceId() {
        return formClass.getId();
    }

    private void navigate(FormViewType viewType) {
        if(!Objects.equals(viewType, this.viewType)) {
            this.viewType = viewType;
            eventBus.fireChange(this);
        }
    }

    public FormViewType getViewType() {
        return viewType;
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
    public VTree getView() {
        return div(BaseStyles.CONTENTPANEL,
            Grid.row(ViewSelector.render(this)),
            Grid.row(getComponent())
        );
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

    public SafeUri viewUri(FormViewType view) {
        return Router.uri(new FormPlace(getResourceId(), view));
    }

    public FormClass getFormClass() {
        return formClass;
    }
}
