package org.activityinfo.ui.style;

public enum ButtonStyle {

    DEFAULT,
    PRIMARY,
    SUCCESS,
    INFO,
    WARNING,
    DANGER,
    LINK;

    public String getClassNames() {
        return "btn btn-" + name().toLowerCase();
    }


}
