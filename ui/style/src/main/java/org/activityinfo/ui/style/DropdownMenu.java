package org.activityinfo.ui.style;

import com.google.common.collect.Lists;
import org.activityinfo.ui.vdom.shared.html.Children;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.activityinfo.ui.vdom.shared.html.H.t;

public class DropdownMenu extends VComponent {

    private String title;
    private List<DropdownMenuItem> items = Lists.newArrayList();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void add(DropdownMenuItem item) {
        items.add(item);
        refresh();
    }

    public void clearItems() {
        items.clear();
        refresh();
    }

    @Override
    protected VTree render() {
        return div(PropMap.withClasses(BaseStyles.DROPDOWN_MENU + " " + BaseStyles.DROPDOWN_MENU_HEAD + " " + BaseStyles.PULL_RIGHT),
            new VNode(HtmlTag.H5, PropMap.withClasses(BaseStyles.TITLE), t(title)),
            new VNode(HtmlTag.UL, PropMap.withClasses(BaseStyles.DROPDOWN_LIST + " " + BaseStyles.GEN_LIST),
                Children.toArray(items)
            ));
    }
}
