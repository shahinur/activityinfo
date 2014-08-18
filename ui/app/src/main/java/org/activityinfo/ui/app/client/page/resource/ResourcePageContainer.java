package org.activityinfo.ui.app.client.page.resource;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.page.Breadcrumb;
import org.activityinfo.ui.app.client.page.PageStore;
import org.activityinfo.ui.store.remote.client.RemoteStoreService;
import org.activityinfo.ui.vdom.client.flux.store.LoadingStatus;
import org.activityinfo.ui.vdom.client.flux.store.RemoteStore;
import org.activityinfo.ui.vdom.client.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.client.flux.store.StoreEventEmitter;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResourcePageContainer implements PageStore, RemoteStore {

    private static final Logger LOGGER = Logger.getLogger(ResourcePageContainer.class.getName());

    private final RemoteStoreService store;
    private final ResourceId resourceId;

    private ResourcePage page;

    private LoadingStatus status = LoadingStatus.PENDING;

    private StoreEventEmitter eventEmitter = new StoreEventEmitter();

    public ResourcePageContainer(RemoteStoreService store, String[] currentPath) {
        this.store = store;
        this.resourceId = ResourceId.valueOf(currentPath[1]);
    }

    @Override
    public void start() {
        load();
    }

    @Override
    public LoadingStatus getLoadingStatus() {
        return status;
    }

    private void load() {
        store.queryTree(resourceId).join(new ResourcePageFactory(store))
                .then(new AsyncCallback<ResourcePage>() {
            @Override
            public void onSuccess(ResourcePage result) {
                status = LoadingStatus.LOADED;
                page = result;
                eventEmitter.fireChange(ResourcePageContainer.this);
            }

            @Override
            public void onFailure(Throwable caught) {
                LOGGER.log(Level.SEVERE, "Load of resource " + resourceId + " failed.", caught);
                status = LoadingStatus.FAILED;
                eventEmitter.fireChange(ResourcePageContainer.this);
            }
        });
    }

    @Override
    public boolean tryHandleNavigation(String[] path) {
        if(path[0].equals("resource") && path[1].equals(resourceId.asString())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void stop() {
        eventEmitter.stop();
        if(page != null) {
            page.stop();
        }
    }

    @Override
    public String getPageTitle() {
        return page.getPageTitle();
    }

    @Override
    public String getPageDescription() {
        return page.getPageDescription();
    }

    @Override
    public Icon getPageIcon() {
        return page.getPageIcon();
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        return page.getBreadcrumbs();
    }

    @Override
    public void addChangeListener(StoreChangeListener listener) {
        eventEmitter.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(StoreChangeListener listener) {
        eventEmitter.removeChangeListener(listener);
    }

    public static SafeUri uri(ResourceId id) {
        return UriUtils.fromTrustedString("#resource/" + id.asString());
    }

    public ResourcePage getPage() {
        return page;
    }
}
