package org.activityinfo.ui.app.client.action;

import org.activityinfo.promise.Promise;
import org.activityinfo.ui.vdom.client.flux.action.Action;
import org.activityinfo.ui.vdom.client.flux.store.Store;

/**
 * Action triggered by navigation to a new url, for example from
 * www.activityinfo.org/ to activityinfo.org/#home
 *
 */
public class NavigateAction implements Action {

    private String path;

    public NavigateAction(String path) {
        this.path = path;
    }

    @Override
    public Promise<Void> accept(Store listener) {
        if(listener instanceof NavigationHandler) {
            ((NavigationHandler) listener).changePath(path);
        }
        return Promise.done();
    }
}
