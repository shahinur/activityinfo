package org.activityinfo.ui.app.client.page.create;

import org.activityinfo.ui.app.client.form.control.HorizontalFormView;
import org.activityinfo.ui.flux.dispatcher.Dispatcher;
import org.activityinfo.ui.style.Alert;
import org.activityinfo.ui.style.AlertStyle;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.vdom.shared.tree.VThunk;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class NewWorkspaceView extends VThunk<NewWorkspaceView> {

    private final NewWorkspacePage page;
    private Dispatcher dispatcher;

    public NewWorkspaceView(Dispatcher dispatcher, NewWorkspacePage page) {
        this.dispatcher = dispatcher;
        this.page = page;
    }

    @Override
    protected VTree render() {
        return div(BaseStyles.CONTENTPANEL,
            Grid.row(
                Grid.column(12,
                introPanel(),
                formPanel() )));

    }


    private VTree introPanel() {
        return new Alert(AlertStyle.INFO,
            p(t("Great! Creating a workspace is the first step in creating an information system for" +
                "your organization, or for organizing your own projects. More info here and here " +
                "and lots of great explanations.")));
    }


    private VTree formPanel() {
        return new HorizontalFormView(dispatcher, page.getInstanceStore());
    }
}
