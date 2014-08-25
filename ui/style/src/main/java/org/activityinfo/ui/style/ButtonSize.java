package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.CssClass;

public enum ButtonSize {
    LG,
    SM,
    XS;

    public CssClass getClassNames() {
        return CssClass.valueOf("btn-" + name().toLowerCase());
    }
}
