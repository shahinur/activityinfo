package org.activityinfo.ui.app.client.page.pivot;

import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.PageFrame;
import org.activityinfo.ui.app.client.page.PageView;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.pivot.tree.FormChooser;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.style.Panel;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.p;

public class PivotPage extends PageView {

    private final Application application;
    private final FormChooser formChooser;
    private final SourcePanel sourcePanel;

    public PivotPage(Application application) {
        this.application = application;
        this.sourcePanel = new SourcePanel();
        this.formChooser = new FormChooser(application);

        sourcePanel.getAddButton().setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                formChooser.setVisible(true);
            }
        });
    }


    @Override
    protected VTree render() {

        Panel previewPanel = new Panel("Preview", p("hello world!"));

        return new PageFrame(FontAwesome.TABLE, "Pivot Table",
            Grid.row(
                Grid.column(3, sourcePanel, formChooser),
                Grid.column(3, previewPanel)
            ));
    }

    @Override
    public boolean accepts(Place place) {
        return place instanceof PivotPlace;
    }
}
