package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.className;

public class Button extends VThunk {

    private ButtonStyle style;
    private ButtonSize size;
    private VTree[] content;


    public static VNode dropDownToggle(VTree icon, VTree label) {
        return new VNode(HtmlTag.BUTTON, PropMap.withClasses(ButtonStyle.DEFAULT.getClassNames() +
                                                             " " + BaseStyles.DROPDOWN_TOGGLE.getClassNames()),
                  new VTree[] { icon, label, caret() });
    }

    public static VNode caret() {
        return new VNode(HtmlTag.SPAN, className(BaseStyles.CARET));
    }


    public Button(ButtonStyle style, ButtonSize size, VTree... content) {
        this.style = style;
        this.size = size;
        this.content = content;
    }

    public Button(ButtonStyle style, VTree... content) {
        this.style = style;
        this.content = content;
    }

    @Override
    protected VTree render(VThunk previous) {
        return new VNode(HtmlTag.BUTTON, PropMap.withClasses(style.getClassNames() + size.getClassNames()), content);
    }
}
