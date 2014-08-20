package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HasClassNames;

public enum AlertStyle implements HasClassNames {

    SUCCESS,
    INFO,
    WARNING,
    DANGER;

    @Override
    public String getClassNames() {
        return "alert alert-" + name().toLowerCase();
    }
}
