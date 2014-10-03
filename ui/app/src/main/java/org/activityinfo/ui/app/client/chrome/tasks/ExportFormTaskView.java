package org.activityinfo.ui.app.client.chrome.tasks;

import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.model.record.RecordBeanClass;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.tasks.ExportFormTaskModel;
import org.activityinfo.service.tasks.ExportFormTaskModelClass;
import org.activityinfo.service.tasks.UserTask;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.request.DownloadBlobRequest;
import org.activityinfo.ui.style.icons.FontAwesome;
import org.activityinfo.ui.vdom.shared.html.Icon;

public class ExportFormTaskView implements TaskView {
    @Override
    public RecordBeanClass getBeanClass() {
        return ExportFormTaskModelClass.INSTANCE;
    }

    @Override
    public Icon getCompleteIcon(UserTask userTask) {
        return FontAwesome.DOWNLOAD;
    }

    @Override
    public String getName(UserTask userTask) {
        return I18N.MESSAGES.exportJobDescription(getModel(userTask).getFilename());
    }

    private ExportFormTaskModel getModel(UserTask userTask) {
        return ExportFormTaskModelClass.INSTANCE.toBean(userTask.getTaskModel());
    }

    @Override
    public String getMessage(UserTask userTask) {
        switch(userTask.getStatus()) {
            default:
            case RUNNING:
                return I18N.CONSTANTS.preparingExport();
            case FAILED:
                return userTask.getErrorMessage();
            case COMPLETE:
                return I18N.CONSTANTS.exportComplete();
        }
    }

    @Override
    public void onClick(Application application, UserTask task) {
        application.getRequestDispatcher().execute(new DownloadBlobRequest(getBlobId(task)));
    }

    private BlobId getBlobId(UserTask task) {
        return BlobId.valueOf(getModel(task).getBlobId());
    }
}
