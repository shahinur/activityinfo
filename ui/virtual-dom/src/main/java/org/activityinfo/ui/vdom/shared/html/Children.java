package org.activityinfo.ui.vdom.shared.html;

import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

public class Children {

    public static VTree[] toArray(List<VTree> children) {
        return children.toArray(new VTree[children.size()]);
    }
}
