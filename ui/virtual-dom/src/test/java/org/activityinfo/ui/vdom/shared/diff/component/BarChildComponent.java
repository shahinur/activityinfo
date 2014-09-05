package org.activityinfo.ui.vdom.shared.diff.component;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.t;

public class BarChildComponent extends VComponent {
    @Override
    protected VTree render() {
        return new VNode(HtmlTag.I, t("he speaks for the trees!"));
    }
}
