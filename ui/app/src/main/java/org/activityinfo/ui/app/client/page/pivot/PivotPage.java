package org.activityinfo.ui.app.client.page.pivot;

import com.google.common.annotations.VisibleForTesting;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.PageFrame;
import org.activityinfo.ui.app.client.page.PageView;
import org.activityinfo.ui.app.client.page.Place;
import org.activityinfo.ui.app.client.page.ResourcePlace;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.app.client.store.InstanceState;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.ClickHandler;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.style.Panel;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class PivotPage extends PageView implements StoreChangeListener {

    private final Application application;
    private final PivotSideBar pivotSideBar;

    private final InstanceState workingDraft;

    private final PivotTablePreview preview;

    public PivotPage(Application application, FormClass formClass, Resource resource) {
        this.application = application;

        this.workingDraft = new InstanceState(application.getDispatcher(), formClass,
            FormInstance.fromResource(resource));

        this.pivotSideBar = new PivotSideBar(application, workingDraft.getState(ResourceId.valueOf("measures")));
        this.pivotSideBar.getSaveButton().setClickHandler(new ClickHandler() {
            @Override
            public void onClicked() {
                save();
            }
        });

        this.preview = new PivotTablePreview(application, workingDraft);
    }


    @VisibleForTesting
    InstanceState getWorkingDraft() {
        return workingDraft;
    }

    @VisibleForTesting
    PivotSideBar getPivotSideBar() {
        return pivotSideBar;
    }

    @Override
    protected void componentDidMount() {
        workingDraft.addChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        this.pivotSideBar.refresh();
    }

    @Override
    protected void componentWillUnmount() {
        workingDraft.removeChangeListener(this);
    }

    @Override
    protected VTree render() {

        return new PageFrame(
            application,
            FontAwesome.TABLE, "Pivot Table",
            Grid.row(
                Grid.column(3, pivotSideBar),
                Grid.column(9, new Panel(preview))
            ));
    }

    private void save() {
        application.getRequestDispatcher().execute(new SaveRequest(workingDraft.getUpdatedResource()));
    }


    @Override
    public boolean accepts(Place place) {
        return place instanceof ResourcePlace;
    }

}
