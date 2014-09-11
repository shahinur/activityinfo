package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.className;
import static org.activityinfo.ui.vdom.shared.html.H.div;

public class Grid {

    /**
     * Creates a row that encloses a node with column spanning the full width
     * of the space.
     */
    public static VNode row(VTree singleColumn) {
        return div(BaseStyles.ROW, div(BaseStyles.COL_XS_12, singleColumn));
    }

    public static VNode row(VTree... columns) {
        return div(BaseStyles.ROW, columns);
    }

    public static VTree column(int span, VTree... children) {
        return div(className("col-md-" + span), children);
    }
}
