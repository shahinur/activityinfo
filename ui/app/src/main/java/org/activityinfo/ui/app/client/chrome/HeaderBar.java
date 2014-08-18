package org.activityinfo.ui.app.client.chrome;

import org.activityinfo.ui.style.Badges;
import org.activityinfo.ui.style.Button;
import org.activityinfo.ui.style.Forms;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.style.icons.GlyphIcons;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.ImmutableThunk;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class HeaderBar extends ImmutableThunk {
    @Override
    public VTree render() {
        return HEADERBAR.div(menuToggle(),
                searchForm(),
                HEADER_RIGHT.div(ul(HEADERMENU,
                                headerMenu(FontAwesome.USER),
                                headerMenu(FontAwesome.ENVELOPE),
                                headerMenu(GlyphIcons.GLOBE),
                                userMenu())));
    }

    private VNode menuToggle() {
        //<a class="menutoggle"><i class="fa fa-bars"></i></a>
        return new VNode(HtmlTag.A, PropMap.withClasses(MENUTOGGLE), FontAwesome.BARS.render());
    }


    private VNode searchForm() {
        return new VNode(HtmlTag.FORM, PropMap.withClasses(SEARCHFORM),
                Forms.textInput("keyword", "Search here"));
    }


    private VNode headerMenu(Icon icon) {
        return li(
                BTN_GROUP.div(
                    button(classNames(BTN, BTN_DEFAULT, DROPDOWN_TOGGLE, TP_ICON),
                        icon.render(),
                        Badges.badge(2))));
    }

    private VNode userMenu() {
        return li(
                BTN_GROUP.div(
                        Button.dropDownToggle(userImage(), t("John Doe"))));

    }

    private VNode userImage() {
        return FontAwesome.USER.render();
    }
}
