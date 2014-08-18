package org.activityinfo.ui.app.client.view.chrome;

import com.google.gwt.dom.client.Style;
import org.activityinfo.ui.app.client.store.AppStores;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Button;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;

import static org.activityinfo.ui.app.client.view.chrome.LeftPanel.leftPanel;
import static org.activityinfo.ui.app.client.view.chrome.MainPanel.mainPanel;
import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.style.PagePreLoader.preLoader;
import static org.activityinfo.ui.vdom.shared.html.H.*;
import static org.activityinfo.ui.vdom.shared.tree.PropMap.withClasses;

public class Chrome {

    public static final String ROOT_ID = "root";

    /**
     * Renders the page skeleton
     */
    public static VNode renderPage(PageContext pageContext, AppStores app) {

        return html(head(meta(charset(UTF_8_CHARSET)),
                        meta(viewport(DEVICE_WIDTH, 1.0, 1.0)),
                        title(pageContext.getApplicationTitle()),
                        link(stylesheet(pageContext.getStylesheetUrl())),
                        script(pageContext.getBootstrapScriptUrl())),
                    theBody(app));
    }

    public static VNode theBody(AppStores app) {
        switch(app.getLoadingStatus()) {
            case PENDING:
                return body(
                        preLoader(),
                        mainSection(app),
                        historyIFrame());
            case LOADED:
                return body(withClasses(PAGE_LOADED),
                        //PagePreLoader.announcement(),
                        mainSection(app),
                        historyIFrame());
            default:
            case FAILED:
                return fullPageError(app.getLoadingFailureDescription());
        }
    }

    public static VNode fullPageError(FailureDescription failure) {

        return body(className(NOTFOUND).setStyle(overflowVisible()),
                section(div(className(NOTFOUNDPANEL),
                                h1(failure.getIcon().render()),
                                h3(failure.getMessage()),
                                h4(failure.getDescription()),
                                form(Button.button("Retry")))));
    }

    private static org.activityinfo.ui.vdom.shared.tree.Style overflowVisible() {
        return style().overflow(Style.Overflow.VISIBLE);
    }

    public static VNode mainSection(AppStores app) {
        return section(id(ROOT_ID),
            leftPanel(app),
            mainPanel(app),
            rightPanel()
        );
    }

    private static VNode historyIFrame() {
        PropMap map = new PropMap();
        map.set("src", "javascript:''");
        map.setId("__gwt_historyFrame");
        map.setStyle(style().setPosition(Style.Position.ABSOLUTE).width(0).height(0).border(0));

        return new VNode(HtmlTag.IFRAME, map);
    }

    private static VNode rightPanel() {
        return BaseStyles.RIGHTPANEL.div();
    }

}
