package org.activityinfo.ui.app.client.page.pivot;

import com.google.common.annotations.VisibleForTesting;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.PageFrame;
import org.activityinfo.ui.app.client.page.PageView;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.store.InstanceState;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.style.Panel;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.p;

public class PivotPage extends PageView implements StoreChangeListener {

    private final Application application;
    private final MeasureList measureList;

    private final InstanceState workingDraft;

    public PivotPage(Application application) {
        this.application = application;
        this.workingDraft = new InstanceState(application.getDispatcher(), PivotTableModel.getFormClass(),
            new FormInstance(Resources.generateId(), PivotTableModel.CLASS_ID));

        this.measureList = new MeasureList(application, workingDraft.getState(ResourceId.valueOf("measures")));
    }


    @VisibleForTesting
    InstanceState getWorkingDraft() {
        return workingDraft;
    }

    @VisibleForTesting
    MeasureList getMeasureList() {
        return measureList;
    }

    @Override
    protected void componentDidMount() {
        workingDraft.addChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        this.measureList.refresh();
    }

    @Override
    protected void componentWillUnmount() {
        workingDraft.removeChangeListener(this);
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
