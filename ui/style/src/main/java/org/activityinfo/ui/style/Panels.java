package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class Panels {

    public static VNode panel(VTree content) {
        return div(PanelStyle.DEFAULT,
                    div(PANEL_BODY, content));
    }

    public static VNode panel(VTree title, VTree... content) {
        return div(PanelStyle.DEFAULT,
                    div(PANEL_HEADING,
                        h3(className(PANEL_TITLE), title)
                    ),
                    div(PANEL_BODY, content));
    }

}
