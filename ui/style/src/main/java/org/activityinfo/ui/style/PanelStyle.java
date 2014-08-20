package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HasClassNames;

public enum PanelStyle implements HasClassNames {

    DEFAULT,
    PRIMARY,
    SUCCESS,
    INFO,
    WARNING,
    DANGER;

    @Override
    public String getClassNames() {
        return "panel panel-" + name().toLowerCase();
    }
}
