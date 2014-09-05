package org.activityinfo.ui.vdom.shared.diff.component;

import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.li;
import static org.activityinfo.ui.vdom.shared.html.H.ul;

public class BarComponent extends VComponent {

    private BarChildComponent child = new BarChildComponent();

    @Override
    protected VTree render() {
        return ul(li(child));
    }
}
