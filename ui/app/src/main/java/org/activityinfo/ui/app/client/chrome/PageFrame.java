package org.activityinfo.ui.app.client.chrome;

import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.CONTENTPANEL;
import static org.activityinfo.ui.style.BaseStyles.PAGEHEADER;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class PageFrame extends VComponent<PageFrame> {

    private Icon pageIcon;
    private String pageTitle;
    private VTree content;

    public PageFrame(Icon pageIcon, String pageTitle, VTree content) {
        this.pageIcon = pageIcon;
        this.pageTitle = pageTitle;
        this.content = content;
    }

    @Override
    protected VTree render() {
        return new VNode(HtmlTag.DIV,
            div(PAGEHEADER, pageHeading()),
            div(CONTENTPANEL, content));
    }


    private VTree pageHeading() {
        return h2(pageIcon(pageIcon), t(pageTitle));
    }

    private static VNode pageIcon(Icon home) {
        return new VNode(HtmlTag.I, PropMap.withClasses(home.getClassNames()));
    }

    @Override
    public String getPropertiesForDebugging() {
        return "pageTitle = " + pageTitle;
    }
}
