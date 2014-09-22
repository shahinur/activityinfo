package org.activityinfo.ui.app.client.chrome;

import com.google.common.collect.Lists;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.vdom.shared.html.Children;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.style.BaseStyles.CONTENTPANEL;
import static org.activityinfo.ui.style.BaseStyles.PAGEHEADER;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class PageFrame extends VComponent<PageFrame> {

    private Icon pageIcon;
    private String pageTitle;
    private VTree[] content;
    private Application application;
    private EditLabelDialog editLabelDialog;

    public PageFrame(Application application, Icon pageIcon, String pageTitle, VTree... content) {
        this(application, pageIcon, pageTitle, null, content);
    }

    public PageFrame(Application application, Icon pageIcon, String pageTitle, EditLabelDialog editLabelDialog, VTree... content) {
        this.pageIcon = pageIcon;
        this.pageTitle = pageTitle;
        this.content = content;
        this.application = application;
        this.editLabelDialog = editLabelDialog;

        if (editLabelDialog != null) {
            editLabelDialog.setLabel(pageTitle);
        }
    }

    @Override
    protected VTree render() {
        return new VNode(HtmlTag.DIV,
                div(PAGEHEADER, pageHeading()),
                div(CONTENTPANEL, content));
    }

    private VTree pageHeading() {
        List<VTree> h2Content = Lists.newArrayList(pageIcon(pageIcon), t(pageTitle));

        if (editLabelDialog != null) { // edit label is not null then attach to component
            h2Content.add(editLabelDialog.createLinkButton());
            h2Content.add(editLabelDialog);
        }

        return h2(Children.toArray(h2Content));
    }

    private static VNode pageIcon(Icon home) {
        return new VNode(HtmlTag.I, PropMap.withClasses(home.getClassNames()));
    }

    @Override
    public String getPropertiesForDebugging() {
        return "pageTitle = " + pageTitle;
    }
}
