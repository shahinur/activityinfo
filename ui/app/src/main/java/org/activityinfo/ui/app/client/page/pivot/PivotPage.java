package org.activityinfo.ui.app.client.page.pivot;

import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.PageFrame;
import org.activityinfo.ui.app.client.page.PageView;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.store.InstanceState;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.style.Panel;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.p;

public class PivotPage extends PageView {

    private final Application application;
    private final MeasureList measureList;

    private final InstanceState workingDraft;

    public PivotPage(Application application) {
        this.workingDraft = new InstanceState(application.getDispatcher(), PivotTableModel.getFormClass(),
            new FormInstance(Resources.generateId(), PivotTableModel.CLASS_ID));
        this.application = application;
        this.measureList = new MeasureList(application, workingDraft.getState(ResourceId.valueOf("measures")));
    }

    @Override
    protected VTree render() {

        Panel previewPanel = new Panel("Preview", p("hello world!"));

        return new PageFrame(FontAwesome.TABLE, "Pivot Table",
            Grid.row(
                Grid.column(3, measureList),
                Grid.column(3, previewPanel)
            ));
    }

    @Override
    public boolean accepts(Place place) {
        return place instanceof PivotPlace;
    }
}
