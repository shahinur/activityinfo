package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.className;

public class CloseButton extends VThunk {
    @Override
    protected VTree render(VThunk previous) {
        //<button aria-hidden="true" data-dismiss="alert" class="close" type="button">×</button>
        return new VNode(HtmlTag.BUTTON, className(BaseStyles.CLOSE).ariaHidden().data("dismiss", "alert"));
    }
}
