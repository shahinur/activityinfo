package org.activityinfo.ui.style;

import com.google.common.collect.Lists;
import org.activityinfo.ui.vdom.shared.html.AriaRole;
import org.activityinfo.ui.vdom.shared.html.Children;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

/**
 * Bootstrap-based horizontal form
 *
 * @see <a href="http://getbootstrap.com/css/#forms-horizontal">Bootstrap Forms</a>
 */
public class HorizontalForm extends VComponent {

    private final List<VTree> formGroups = Lists.newArrayList();


    public HorizontalForm addGroup(VTree group) {
        this.formGroups.add(group);
        return this;
    }

    @Override
    public VTree render() {
        return new VNode(HtmlTag.FORM,
            PropMap.withClasses(BaseStyles.FORM_HORIZONTAL).role(AriaRole.FORM),
            Children.toArray(formGroups));
    }
}
