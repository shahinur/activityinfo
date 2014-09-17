package org.activityinfo.ui.vdom.shared.html;

import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.ArrayList;
import java.util.List;

public class Children {

    public static VTree[] toArray(List<? extends VTree> children) {
        return children.toArray(new VTree[children.size()]);
    }

    public static VTree[] toArraySkipNull(List<VTree> children) {
        List<VTree> nonNull = new ArrayList<>();
        for(VTree child : children) {
            if(child != null) {
                children.add(child);
            }
        }
        return toArray(nonNull);
    }
}
