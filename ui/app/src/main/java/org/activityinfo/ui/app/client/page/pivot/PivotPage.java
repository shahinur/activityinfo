package org.activityinfo.ui.app.client.page.pivot;

import com.google.common.annotations.VisibleForTesting;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.analysis.PivotTableModel;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.UpdateResult;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.chrome.PageFrame;
import org.activityinfo.ui.app.client.page.PageView;
import org.activityinfo.ui.app.client.request.SaveRequest;
import org.activityinfo.ui.app.client.store.InstanceState;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.style.*;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.t;

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

        Button saveButton = pivotSideBar.getSaveButton();
        if(workingDraft.isDirty()) {
            saveButton.setContent(t(I18N.CONSTANTS.save()));
            saveButton.setEnabled(true);
        } else {
            saveButton.setContent(t(I18N.CONSTANTS.saved()));
            saveButton.setEnabled(false);
        }
        saveButton.refresh();

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
            FontAwesome.TABLE, getLabel(),
            Grid.row(
                Grid.column(3, pivotSideBar),
                Grid.column(9, new Panel(preview))
            ));
    }

    private String getLabel() {
        FieldValue labelValue = workingDraft.getState(ResourceId.valueOf(PivotTableModel.LABEL_FIELD_ID)).getValue();
        if(labelValue instanceof TextValue) {
            return ((TextValue) labelValue).asString();
        }
        return "New Pivot Table";
    }

    private void save() {
        final Promise<UpdateResult> response = application.getRequestDispatcher().execute(
            new SaveRequest(workingDraft.getUpdatedResource()));
        final Button button = pivotSideBar.getSaveButton();
        button.setContent(Spinners.spinner().render(), t(I18N.CONSTANTS.saving()));
        response.then(new AsyncCallback<UpdateResult>() {
            @Override
            public void onFailure(Throwable caught) {
                button.setStyle(ButtonStyle.DANGER);
                button.setContent(t(I18N.CONSTANTS.retry()));
                button.setEnabled(true);
                button.refresh();
            }

            @Override
            public void onSuccess(UpdateResult result) {
                button.setStyle(ButtonStyle.PRIMARY);
                button.setContent(t(I18N.CONSTANTS.saved()));
                button.setEnabled(true);
                button.refresh();
            }
        });
    }
}
