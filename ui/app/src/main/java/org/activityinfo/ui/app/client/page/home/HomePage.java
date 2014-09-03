package org.activityinfo.ui.app.client.page.home;

import org.activityinfo.ui.app.client.page.Breadcrumb;
import org.activityinfo.ui.app.client.page.PageStore;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.flux.store.LoadingStatus;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.VThunk;

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
    public VThunk getView() {
        return new HomeView();
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
    public LoadingStatus getLoadingStatus() {
        return LoadingStatus.LOADED;
    }

}
