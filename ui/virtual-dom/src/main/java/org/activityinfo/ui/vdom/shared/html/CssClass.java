package org.activityinfo.ui.vdom.shared.html;

public final class CssClass {

    private final String classNames;

    protected CssClass(String classNames) {
        this.classNames = classNames;
    }

    public static CssClass valueOf(String classNames) {
        return new CssClass(classNames);
    }

    public String getClassNames() {
        return classNames;
    }

    @Override
    public String toString() {
        return classNames;
    }
}
