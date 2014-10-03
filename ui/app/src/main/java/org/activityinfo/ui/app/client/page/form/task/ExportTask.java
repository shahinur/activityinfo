package org.activityinfo.ui.app.client.page.form.task;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.tasks.ExportFormTaskModel;
import org.activityinfo.service.tasks.ExportFormTaskModelClass;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.page.folder.task.Task;
import org.activityinfo.ui.app.client.request.StartTask;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

public class ExportTask implements Task {

    private Application application;
    private ResourceId formClassId;

    public ExportTask(Application application, ResourceId formClassId) {
        this.application = application;
        this.formClassId = formClassId;
    }

    @Override
    public String getLabel() {
        return I18N.CONSTANTS.export();
    }

    @Override
    public Icon getIcon() {
        return FontAwesome.FILE_EXCEL_O;
    }

    @Override
    public void onClicked() {
        ExportFormTaskModel taskModel = new ExportFormTaskModel();
        taskModel.setFormClassId(formClassId);
        taskModel.setBlobId(BlobId.generate());
        taskModel.setFilename(formClassId.asString() + ".csv");

        application.getRequestDispatcher().execute(new StartTask(taskModel, ExportFormTaskModelClass.INSTANCE));
    }
}
