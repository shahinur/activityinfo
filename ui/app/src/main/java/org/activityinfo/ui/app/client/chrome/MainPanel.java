package org.activityinfo.ui.app.client.chrome;

import org.activityinfo.ui.app.client.page.PageStore;
import org.activityinfo.ui.app.client.store.Application;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class MainPanel {

    public static VNode mainPanel(Application app) {
        return div(MAINPANEL,
                HeaderBar.render(),
                div(PAGEHEADER, pageHeading(app)), contentPanel(app));
    }

    private static VTree pageHeading(Application app) {

        PageStore page = app.getRouter().getActivePage();

        switch (page.getLoadingStatus()) {
            case PENDING:
                return h2( pageIcon(FontAwesome.SPINNER), t("Loading...") );
            case LOADED:
                if(page.getPageDescription() == null) {
                    return h2( pageIcon(page.getPageIcon()), t(page.getPageTitle()));
                } else {
                    return h2( pageIcon(page.getPageIcon()), t(page.getPageTitle()),
                                span(SUBTITLE, page.getPageDescription()));
                }

            default:
            case FAILED:
                return new VNode(HtmlTag.DIV);
        }
    }

    private static VNode pageIcon(Icon home) {
        return new VNode(HtmlTag.I, PropMap.withClasses(home.getClassNames()));
    }

    private static VTree contentPanel(Application app) {
        PageStore activePage = app.getRouter().getActivePage();
        return activePage.getView();
    }
//
//    private static VTree renderResource(ResourcePageContainer container) {
//        if(container.getLoadingStatus() == LoadingStatus.LOADED) {
//            ResourcePage page = container.getPage();
//
//            if (page instanceof FolderPage) {
//                return FolderView.render((FolderPage) page);
//
//            } else if(page instanceof FormPage) {
//                return FormView.render((FormPage) page);
//
//            } else {
//                return div("todo");
//            }
//        } else {
//            return div("");
//        }
//    }
}
