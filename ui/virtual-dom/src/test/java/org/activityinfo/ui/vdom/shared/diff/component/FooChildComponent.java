package org.activityinfo.ui.vdom.shared.diff.component;

import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.activityinfo.ui.vdom.shared.html.H.t;

public class FooChildComponent extends VComponent {

    private final String name;

    public FooChildComponent(String name) {
        this.name = name;
    }

    @Override
    protected VTree render() {
        return div("blue", t(name));
    }
}
