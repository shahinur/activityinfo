package org.activityinfo.ui.app.client.chrome;

import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.PAGEHEADER;
import static org.activityinfo.ui.style.BaseStyles.SUBTITLE;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class MainPanel {

    public static VNode mainPanel() {
        return BaseStyles.MAINPANEL.div(
                new HeaderBar(),
                pageHeader(),
                contentPanel());
    }

    private static VTree pageHeader() {
        return div(PAGEHEADER,
                h2( pageHeaderIcon(FontAwesome.HOME),
                        H.t("Dashboard"),
                        span(SUBTITLE, "Subtitle goes here"))
        );
    }

    private static VNode pageHeaderIcon(Icon home) {
        return new VNode(HtmlTag.I, PropMap.withClasses(home.getClassNames()));
    }

    private static VNode contentPanel() {
        return BaseStyles.CONTENTPANEL.div();
    }

}
