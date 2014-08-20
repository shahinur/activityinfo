package org.activityinfo.ui.app.client.page.form;

import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.vdom.shared.tree.VTree;
import org.activityinfo.ui.vdom.shared.tree.VWidget;

import static org.activityinfo.ui.vdom.shared.html.H.div;

public class FormView {

    public static VTree render(FormPage page) {

        return div(BaseStyles.CONTENTPANEL,
                    Grid.row(ViewSelector.render(page)),
                    Grid.row(createWidget(page))
                );
    }

    private static VWidget createWidget(FormPage page) {
        switch(page.getViewType()) {
            case DESIGN:
                return new FormDesignerWidget(page);

            default:
            case OVERVIEW:
            case TABLE:
                return new FormTableWidget(page);
        }
    }

}
