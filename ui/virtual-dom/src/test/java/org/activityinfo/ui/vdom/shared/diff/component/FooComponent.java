package org.activityinfo.ui.vdom.shared.diff.component;

import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.Arrays;
import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.li;
import static org.activityinfo.ui.vdom.shared.html.H.ul;

public class FooComponent extends VComponent {

    private FooChildComponent franky = new FooChildComponent("Franky");
    private FooChildComponent bobby = new FooChildComponent("Bobby");

    @Override
    protected VTree render() {
        return ul(li(franky), li(bobby));
    }

    public List<FooChildComponent> getChildren() {
        return Arrays.asList(franky, bobby);
    }

}
