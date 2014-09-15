package org.activityinfo.ui.vdom.shared.html;

import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;

public final class Icon {
    private String classNames;

    public static Icon valueOf(String classNames) {
        return new Icon(classNames);
    }

    private Icon(String classNames) {
        this.classNames = classNames;
    }

    public final VNode render() {
        return new VNode(HtmlTag.SPAN, PropMap.withClasses(classNames));
    }

    public Icon appendClass(String classToAppend) {
        classNames += " " + classToAppend;
        return this;
    }

    public Icon pullRight() {
        appendClass("pull-right");
        return this;
    }

    public String getClassNames() {
        return classNames;
    }

    public CssClass getCssClass() {
        return CssClass.valueOf(classNames);
    }
}
