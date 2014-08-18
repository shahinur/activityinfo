package org.activityinfo.ui.app.client.chrome;

import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class LeftPanel {


    public static VNode leftPanel() {
        return LEFTPANEL.div(logoPanel(), innerPanel());
    }


    private static VNode logoPanel() {
        return LOGOPANEL.div(
                h1(span("["), H.t(" activityinfo "), span("]")));
    }

    private static VTree innerPanel() {
        return LEFTPANELINNER.div(
                sidebarTitle("Workspaces"),
                navigationMenu());
    }

    private static VNode sidebarTitle(String title) {
        return new VNode(HtmlTag.H5, PropMap.withClasses(SIDEBARTITLE), t(title));
    }

    private static VNode navigationMenu() {
        return ul(classNames(NAV, NAV_PILLS, NAV_STACKED, NAV_BRACKET),
                navItem(UriUtils.fromTrustedString("#"), FontAwesome.HOME, "Home"),
                navItem(UriUtils.fromTrustedString("#"), FontAwesome.HOME, "Data Entry"));
    }


    private static VNode navItem(SafeUri url, Icon icon, String label) {
        return li(link(url, icon.render(), space(), span(label)));
    }
}
