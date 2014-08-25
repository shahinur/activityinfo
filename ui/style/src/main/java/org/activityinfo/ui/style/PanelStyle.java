package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.CssClass;

public enum PanelStyle {

    DEFAULT,
    PRIMARY,
    SUCCESS,
    INFO,
    WARNING,
    DANGER;

    public CssClass getClassNames() {
        return CssClass.valueOf("panel panel-" + name().toLowerCase());
    }
}
