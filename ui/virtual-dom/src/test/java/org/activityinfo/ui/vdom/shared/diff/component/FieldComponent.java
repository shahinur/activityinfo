package org.activityinfo.ui.vdom.shared.diff.component;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VText;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class FieldComponent extends VComponent<FieldComponent> {


    private String name;

    FieldComponent(String name) {
        this.name = name;
    }

    @Override
    protected VTree render() {
        return new VNode(HtmlTag.LI, new VText(name));
    }

    @Override
    protected void componentWillMount() {
    }

    @Override
    public String getPropertiesForDebugging() {
        return name;
    }
}
