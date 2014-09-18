package org.activityinfo.ui.app.client.page.form;

import com.google.gwt.safehtml.shared.SafeUri;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.nav.NavLink;
import org.activityinfo.ui.app.client.draft.Draft;
import org.activityinfo.ui.app.client.page.PagePreLoader;
import org.activityinfo.ui.app.client.page.PageView;
import org.activityinfo.ui.app.client.page.PageViewFactory;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.store.Router;
import org.activityinfo.ui.flux.store.Status;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.flux.store.StoreEventBus;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.CONTENTPANEL;
import static org.activityinfo.ui.style.BaseStyles.PAGEHEADER;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class FormPage extends PageView implements StoreChangeListener {

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

    private Application application;
    private FormViewType viewType = FormViewType.OVERVIEW;
    private ResourceId resourceId;

    private StoreEventBus eventBus;
    private Promise<FormTree> formTree;

    public FormPage(Application application, ResourceId resourceId) {
        this.application = application;
        this.resourceId = resourceId;

        FormPlace currentPlace = application.getRouter().getCurrentPlace();
        this.viewType = currentPlace.getFormViewType();
    }

    @Override
    public void componentDidMount() {
        application.getDraftStore().addChangeListener(this);
        application.getResourceStore().addChangeListener(this);
        application.getRouter().addChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        refresh();
    }

    @Override
    protected void componentWillUnmount() {
        application.getDraftStore().removeChangeListener(this);
        application.getResourceStore().removeChangeListener(this);
        application.getRouter().removeChangeListener(this);
    }

    public ResourceId getResourceId() {
        return resourceId;
    }

    public FormViewType getViewType() {
        return viewType;
    }

    private VTree getComponent() {
        switch (getViewType()) {
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
        Status<Draft> draftStatus = application.getDraftStore().get(getResourceId());
        if (draftStatus.isAvailable()) {
            return FormClass.fromResource(draftStatus.get().getResource());
        }
        Status<FormClass> formClassStatus = application.getResourceStore().getFormClass(getResourceId());
        if (formClassStatus.isAvailable()) {
            return formClassStatus.get();
        }
        return null;
    }

    @Override
    protected VTree render() {
        if (getFormClass() == null) { // still loading
            return new PagePreLoader();
        }
        return new VNode(HtmlTag.DIV,
                div(PAGEHEADER, formHeading()),
                div(CONTENTPANEL, navTabs(), tabPane()));
    }

    private VTree tabPane() {
        return div(BaseStyles.TAB_CONTENT,
                div(PropMap.withClasses(BaseStyles.TAB_PANE)), getComponent());
    }

    private VTree navTabs() {
        final NavLink designTab = new NavLink(application.getRouter());
        designTab.setIcon(FontAwesome.PENCIL);
        designTab.setLabel("Design");
        designTab.setTarget(new FormPlace(getResourceId(), FormViewType.DESIGN));

        final NavLink tableTab = new NavLink(application.getRouter());
        tableTab.setIcon(FontAwesome.TABLE);
        tableTab.setLabel("Table");
        tableTab.setTarget(new FormPlace(getResourceId(), FormViewType.TABLE));

        return ul(classNames(BaseStyles.NAV, BaseStyles.NAV_TABS, BaseStyles.NAV_DARK),
                tableTab, designTab
        );
    }

    private VTree formHeading() {
        return h2(new VTree[]{FontAwesome.FILE.render(), t(getFormClass().getLabel())});
    }
}
