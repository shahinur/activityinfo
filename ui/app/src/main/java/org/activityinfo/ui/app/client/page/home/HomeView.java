package org.activityinfo.ui.app.client.page.home;

import org.activityinfo.ui.style.Alert;
import org.activityinfo.ui.style.AlertStyle;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class HomeView extends VThunk {

    @Override
    protected VTree render() {
        return div(BaseStyles.CONTENTPANEL,
                Grid.row( announcement() ) );
    }


    public static VTree announcement() {
        return new Alert(AlertStyle.INFO,
                h4("Welcome to Activity 3.0 Beta!"),
                p(strong("Please note"),
                        t(" that not all functionality is yet available in this beta; we look forward " +
                          " to your feedback and are working to completing the migration as soon as possible.")));


    }

}
