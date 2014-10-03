package org.activityinfo.ui.app.client.chrome;

import com.google.common.collect.Lists;
import org.activityinfo.ui.app.client.dialogs.ConfirmDialog;
import org.activityinfo.ui.app.client.dialogs.DeleteResourceAction;
import org.activityinfo.ui.app.client.dialogs.RenameResourceDialog;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Button;
import org.activityinfo.ui.style.ButtonStyle;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.style.icons.FontAwesome;
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
    private PageFrameConfig config;

    public PageFrame(Icon pageIcon, String pageTitle, VTree... content) {
        this(pageIcon, pageTitle, new PageFrameConfig(), content);
    }

    public PageFrame(Icon pageIcon, String pageTitle, PageFrameConfig config, VTree... content) {
        this.pageIcon = pageIcon;
        this.pageTitle = pageTitle;
        this.content = content;
        this.config = config;
    }

    @Override
    protected VTree render() {
        return new VNode(HtmlTag.DIV,
                div(PAGEHEADER, pageHeading()),
                div(CONTENTPANEL, content));
    }

    private VTree pageHeading() {
        List<VTree> h2Content = Lists.newArrayList(pageIcon(pageIcon), t(pageTitle));

        DeleteResourceAction enableDeletion = config.enableDeletion();
        if (enableDeletion != null && config.isEditAllowed()) {
            ConfirmDialog confirmDialog = ConfirmDialog.confirm(enableDeletion);
            h2Content.add(confirmDialog);
            h2Content.add(div(BaseStyles.PULL_RIGHT, deleteButton(confirmDialog)));
        }

        RenameResourceDialog enableRename = config.getEnableRename();
        if (enableRename != null && config.isEditAllowed()) {
            enableRename.setLabel(pageTitle);
            h2Content.add(enableRename);
            h2Content.add(div(BaseStyles.PULL_RIGHT, enableRename.createLinkButton()));
        }

        return h2(Children.toArray(h2Content));
    }

    public Button deleteButton(final ConfirmDialog confirmDialog) {
        Button button = new Button(ButtonStyle.LINK, FontAwesome.TRASH_O.render());
        button.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                confirmDialog.setVisible(true);
            }
        });
        return button;
    }

    private static VNode pageIcon(Icon home) {
        return new VNode(HtmlTag.I, PropMap.withClasses(home.getClassNames()));
    }

    @Override
    public String getPropertiesForDebugging() {
        return "pageTitle = " + pageTitle;
    }
}
