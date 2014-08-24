package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class Panel extends VThunk {

    private PanelStyle style;
    private VTree title;
    private VTree content;

    public Panel(VTree content) {
        this.style = PanelStyle.DEFAULT;
        this.content = content;
    }

    public Panel(String title, VTree content) {
        this.style = PanelStyle.DEFAULT;
        this.title = t(title);
        this.content = content;
    }

    @Override
    protected VTree render(VThunk previous) {
        VTree children[];
        if(title == null) {
            children = new VTree[]{body()};
        } else {
            children = new VTree[]{body(), heading()};
        }

        return div(PanelStyle.DEFAULT.getClassNames(), children);
    }


    private VTree heading() {
        return div(PANEL_HEADING, h3(className(PANEL_TITLE), title));
    }

    private VNode body() {
        return div(PANEL_BODY, content);
    }

}
