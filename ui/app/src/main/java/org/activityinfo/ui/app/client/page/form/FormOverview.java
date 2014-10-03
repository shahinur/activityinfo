package org.activityinfo.ui.app.client.page.form;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.folder.task.TasksPanel;
import org.activityinfo.ui.app.client.page.form.task.ExportTask;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.BaseStyles;
import org.activityinfo.ui.style.Grid;
import org.activityinfo.ui.style.Panel;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.activityinfo.ui.vdom.shared.html.H.p;

public class FormOverview extends VComponent implements StoreChangeListener {

    private final Application application;
    private final ResourceId resourceId;

    private final TasksPanel exportPanel;

    public FormOverview(Application application, ResourceId resourceId) {
        this.application = application;
        this.resourceId = resourceId;

        this.exportPanel = new TasksPanel(I18N.CONSTANTS.export(), new ExportTask(application, resourceId));
    }

    @Override
    protected void componentDidMount() {
        application.getResourceStore().addChangeListener(this);
    }

    @Override
    protected VTree render() {
        return div(BaseStyles.ROW,
            Grid.column(9, new Panel("Todo", p("Todo"))),
            Grid.column(3, exportPanel));
    }

    @Override
    public void onStoreChanged(Store store) {
        refresh();
    }

    @Override
    protected void componentWillUnmount() {
        application.getResourceStore().removeChangeListener(this);
    }
}
