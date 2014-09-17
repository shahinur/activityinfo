package org.activityinfo.ui.app.client.chrome;

import com.google.common.collect.Lists;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.*;
import org.activityinfo.ui.app.client.page.create.NewWorkspacePage;
import org.activityinfo.ui.app.client.page.folder.FolderPage;
import org.activityinfo.ui.app.client.page.home.HomePage;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.style.BaseStyles.MAINPANEL;
import static org.activityinfo.ui.vdom.shared.html.H.div;

public class MainPanel extends VComponent<MainPanel> implements StoreChangeListener {

    private final Application application;

    /**
     * Page views are singletons
     */
    private final List<PageViewFactory<?>> pageFactories = Lists.newArrayList();

    private final HeaderBar headerBar;

    private PageView pageView;

    public MainPanel(Application application) {
        this.application = application;

        headerBar = new HeaderBar(application);

        pageFactories.add(new NewWorkspacePage.Factory(application));
        pageFactories.add(new FolderPage.Factory(application));
        pageFactories.add(new ResourcePageView.Factory(application));
        pageFactories.add(new HomePage.Factory(application));

        pageView = pageViewForCurrentPlace();
    }

    @Override
    public void componentDidMount() {
        application.getRouter().addChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        PageView newView = pageViewForCurrentPlace();
        if(newView != pageView) {
            pageView = newView;
            refresh();
        }
    }

    @Override
    protected VTree render() {
        return div(MAINPANEL, headerBar, pageView);
    }

    private PageView pageViewForCurrentPlace() {

        Place place = application.getRouter().getCurrentPlace();

        for(PageViewFactory factory : pageFactories) {
            if(factory.accepts(place)) {
                return factory.create(place);
            }
        }
        return new NotFoundPageView();
    }

}
