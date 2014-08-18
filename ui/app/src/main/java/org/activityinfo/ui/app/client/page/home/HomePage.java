package org.activityinfo.ui.app.client.page.home;

import org.activityinfo.ui.app.client.page.Breadcrumb;
import org.activityinfo.ui.app.client.page.PageStore;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.client.flux.store.LoadingStatus;
import org.activityinfo.ui.vdom.client.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.shared.html.Icon;

import java.util.Collections;
import java.util.List;

public class HomePage implements PageStore {


    @Override
    public String getPageTitle() {
        return "Home";
    }

    @Override
    public String getPageDescription() {
        return null;
    }

    @Override
    public List<Breadcrumb> getBreadcrumbs() {
        return Collections.emptyList();
    }

    @Override
    public Icon getPageIcon() {
        return FontAwesome.HOME;
    }

    @Override
    public void stop() {

    }

    @Override
    public void start() {

    }

    @Override
    public boolean tryHandleNavigation(String[] path) {
        return path.length == 0;
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
}
