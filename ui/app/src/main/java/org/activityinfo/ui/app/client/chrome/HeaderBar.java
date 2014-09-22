package org.activityinfo.ui.app.client.chrome;

import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.connectivity.ConnectivityWidget;
import org.activityinfo.ui.app.client.chrome.tasks.TaskIcon;
import org.activityinfo.ui.style.Badges;
import org.activityinfo.ui.style.InputControlType;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.style.Buttons.dropDownToggle;
import static org.activityinfo.ui.style.Forms.input;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class HeaderBar extends VComponent<HeaderBar> {

    private Application application;

    public HeaderBar(Application application) {
        this.application = application;
    }

    @Override
    public VTree render() {
        return div(HEADERBAR, menuToggle(),
                searchForm(),
                div(HEADER_RIGHT,
                    ul(HEADERMENU,
                        headerMenu(FontAwesome.USER),
                        headerMenu(FontAwesome.ENVELOPE),
                        li(new TaskIcon(application)),
                        li(new ConnectivityWidget(application)),
                        userMenu())));
    }

    private static VNode menuToggle() {
        //<a class="menutoggle"><i class="fa fa-bars"></i></a>
        return new VNode(HtmlTag.A, PropMap.withClasses(MENUTOGGLE), FontAwesome.BARS.render());
    }


    private static VNode searchForm() {
        return form(className(SEARCHFORM),
                input(InputControlType.TEXT, "keyword", "Search here"));
    }

    private static VNode headerMenu(Icon icon) {
        return li(
                div(className(BTN_GROUP),
                        new VNode(HtmlTag.BUTTON, classNames(BTN, BTN_DEFAULT, DROPDOWN_TOGGLE, TP_ICON),
                                new VTree[] { icon.render(),
                                            Badges.badge(2) })));
    }

    private static VNode userMenu() {
        return li(
                div(className(BTN_GROUP),
                        dropDownToggle(userImage(), t("John Doe"))));

    }

    private static VNode userImage() {
        return FontAwesome.USER.render();
    }
}
