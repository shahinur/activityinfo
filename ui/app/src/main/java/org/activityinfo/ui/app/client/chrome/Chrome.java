package org.activityinfo.ui.app.client.chrome;

import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.tree.VNode;

import static org.activityinfo.ui.vdom.shared.html.H.button;
import static org.activityinfo.ui.vdom.shared.html.H.span;

public class Chrome {

    public static VNode mainWrapper() {
        return H.section(
                LeftPanel.leftPanel(),
                MainPanel.mainPanel(),
                rightPanel());
    }

    private static VNode rightPanel() {
        return BaseStyles.RIGHTPANEL.div();
    }

}
