package org.activityinfo.ui.app.client.page;

import org.activityinfo.ui.flux.store.LoadingStatus;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.List;

public interface PageStore extends Store {


    String getPageTitle();

    String getPageDescription();

    Icon getPageIcon();

    List<Breadcrumb> getBreadcrumbs();

    void stop();

    void start();

    /**
     *
     * @return the loading status of this page.
     */
    LoadingStatus getLoadingStatus();


    boolean tryHandleNavigation(String[] path);
}
