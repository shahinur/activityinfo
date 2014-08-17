package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HasClassNames;

public enum ButtonStyle implements HasClassNames {

    DEFAULT,
    PRIMARY,
    SUCCESS,
    INFO,
    WARNING,
    DANGER,
    LINK;

    @Override
    public String getClassNames() {
        return "btn btn-" + name().toLowerCase();
    }


}
