package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.CssClass;

public enum AlertStyle {

    SUCCESS,
    INFO,
    WARNING,
    DANGER;

    public CssClass getClassNames() {
        return CssClass.valueOf("alert alert-" + name().toLowerCase());
    }
}
