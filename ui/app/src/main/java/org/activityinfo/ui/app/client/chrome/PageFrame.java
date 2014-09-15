package org.activityinfo.ui.app.client.chrome;

import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.create.NewWorkspacePlace;
import org.activityinfo.ui.style.*;
import org.activityinfo.ui.style.icons.FontAwesome;
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
    private Application application;

    public PageFrame(Icon pageIcon, String pageTitle, VTree content, Application application) {
        this.pageIcon = pageIcon;
        this.pageTitle = pageTitle;
        this.content = content;
        this.application = application;
    }

    @Override
    protected VTree render() {
        return new VNode(HtmlTag.DIV,
                div(PAGEHEADER, pageHeading()),
                div(CONTENTPANEL, content));
    }

    private VTree pageHeading() {
        return h2(pageIcon(pageIcon), t(pageTitle),
                deleteButton(), addButton());
    }

    private VTree deleteButton() {
        Button button = new Button(ButtonStyle.DEFAULT, ButtonSize.XS, FontAwesome.TRASH_O.render()).addCssClass(BaseStyles.PULL_RIGHT);
        button.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                //todo
            }
        });
        return button;
    }

    private VTree addButton() {
        Button button = new Button(ButtonStyle.DEFAULT, ButtonSize.XS, FontAwesome.PLUS.render()).addCssClass(BaseStyles.PULL_RIGHT);
        button.setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                //todo
                application.getRouter().navigate(NewWorkspacePlace.INSTANCE);
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
