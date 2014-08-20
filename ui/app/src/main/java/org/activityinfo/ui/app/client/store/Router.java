package org.activityinfo.ui.app.client.store;

import org.activityinfo.ui.app.client.page.PageStore;
import org.activityinfo.ui.app.client.page.home.HomePage;
import org.activityinfo.ui.app.client.page.resource.ResourcePageContainer;
import org.activityinfo.service.store.RemoteStoreService;
import org.activityinfo.ui.vdom.client.flux.store.Store;
import org.activityinfo.ui.vdom.client.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.client.flux.store.StoreEventEmitter;

import java.util.Arrays;

/**
 * Tracks the current "route" or internal URL within the application,
 * and manages the starting and stopping of "stores" associated with
 * current activity.
 *
 */
public class Router implements Store {

    private final StoreEventEmitter emitter = new StoreEventEmitter();

    private String[] currentPath = new String[0];

    private PageStore activePage = new HomePage();
    private RemoteStoreService service;

    public Router(RemoteStoreService service) {
        this.service = service;
    }

    public void updatePath(String token) {
        String[] path = parseToken(token);
        if(!Arrays.equals(path, currentPath)) {
            navigateAndFire(path);
        }
    }

    private String[] parseToken(String token) {
        if(token.length() == 0) {
            return new String[0];
        } else {
            return token.split("/");
        }
    }

    private void navigateAndFire(String[] path) {
        this.currentPath = path;
        if(currentPath.length == 0 && !(activePage instanceof HomePage)) {
            navigate(new HomePage());
        } else {
            // Will the current page handle the navigation?
            if(activePage != null && activePage.tryHandleNavigation(path)) {
                // great! done.
                return;
            }
            // switch out the current page
            navigate(new ResourcePageContainer(service, currentPath));
        }
        emitter.fireChange(this);
    }

    private void navigate(PageStore homePage) {
        if(activePage != null) {
            activePage.stop();
        }
        activePage = homePage;
        activePage.start();
    }

    @Override
    public void addChangeListener(StoreChangeListener listener) {
        emitter.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(StoreChangeListener listener) {
        emitter.removeChangeListener(listener);
    }

    public PageStore getActivePage() {
        return activePage;
    }
}
