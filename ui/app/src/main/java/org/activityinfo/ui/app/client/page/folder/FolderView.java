package org.activityinfo.ui.app.client.page.folder;

import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.ResourceNode;
import org.activityinfo.ui.app.client.page.resource.ResourcePageContainer;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.style.Media;
import org.activityinfo.ui.style.Panel;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.Style;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class FolderView {

    public static VTree render(FolderPage page) {

        return div(BaseStyles.CONTENTPANEL,
                div(BaseStyles.ROW,
                        listColumn(page),
                        timelineColumn(),
                        helpColumn()));
    }

    private static VTree listColumn(FolderPage page) {

        return Grid.column(5,  childTable(page));

    }

    private static VTree childTable(FolderPage page) {
        return new Panel(
        div(className(BaseStyles.WIDGET_BLOGLIST), map(page.getChildNodes(), new Render<ResourceNode>() {
                    @Override
                    public VTree render(ResourceNode item) {
                        return media(item);
                    }
                })));
    }

    private static VTree media(ResourceNode child) {
        return Media.media(childIcon(child),
                ResourcePageContainer.uri(child.getId()),
                t(child.getLabel()), description(child));
    }

    private static VTree description(ResourceNode child) {
        return t("Something descriptive here");
    }

    private static VTree childIcon(ResourceNode child) {
        Icon icon;
        if(FormClass.CLASS_ID.equals(child.getClassId())) {
            icon = FontAwesome.EDIT;
        } else {
            icon = FontAwesome.FOLDER_OPEN_O;
        }

        Style iconStyle = new Style().fontSize(50).lineHeight(50);
        PropMap iconProps = new PropMap();
        iconProps.setStyle(iconStyle);
        iconProps.setClass(icon.getCssClass());

        return div(className(BaseStyles.MEDIA_OBJECT), span(iconProps));
    }

    private static VNode childIcon() {
        return FontAwesome.FOLDER_OPEN_O.render();
    }

    private static VTree timelineColumn() {
        return Grid.column(4, new Panel("Recent Activity", p("Todo...")));
    }

    private static VTree helpColumn() {
        return Grid.column(3,
                new Panel("Common Tasks", p("Todo...")),
                new Panel("Administration", p("Todo..."))
                );
    }

}
