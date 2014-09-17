package org.activityinfo.ui.style;

import com.google.common.collect.Lists;
import org.activityinfo.ui.vdom.shared.html.Children;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.ul;

public class Toolbar extends VComponent {

    private List<VComponent> items = Lists.newArrayList();


    public void add(ToolItem item) {
        items.add(item);
    }

    @Override
    protected VTree render() {
        return ul(BaseStyles.FILEMANAGER_OPTIONS, Children.toArray(items));
    }
}
